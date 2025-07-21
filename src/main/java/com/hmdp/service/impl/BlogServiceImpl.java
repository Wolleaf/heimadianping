package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.constant.RedisConstants;
import com.hmdp.constant.SystemConstants;
import com.hmdp.domain.dto.ScrollResult;
import com.hmdp.domain.dto.UserDTO;
import com.hmdp.domain.entity.Blog;
import com.hmdp.domain.entity.Follow;
import com.hmdp.domain.entity.User;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IBlogService;
import com.hmdp.service.IFollowService;
import com.hmdp.service.IUserService;
import com.hmdp.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@RequiredArgsConstructor
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    private final IUserService userService;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserMapper userMapper;
    private final IFollowService followService;

    @Override
    public List<Blog> queryHotBlog(Integer current) {
        // 根据用户查询
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(blog -> {
            fillBlogWithUser(blog);
            setBlogIsLiked(blog);
        });
        return records;
    }

    private void fillBlogWithUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }

    @Override
    public Blog queryBlogById(Long id) {
        Blog blog = baseMapper.selectById(id);
        fillBlogWithUser(blog);
        setBlogIsLiked(blog);
        return blog;
    }

    private void setBlogIsLiked(Blog blog) {
        // 查看当前用户是否点赞
        UserDTO user = UserHolder.getUser();
        // 用户未登录
        if (user == null) {
            return;
        }
        String userId = UserHolder.getUser().getId().toString();
        String key = RedisConstants.BLOG_LIKED_KEY + blog.getId();
        Double isMember = stringRedisTemplate.opsForZSet().score(key, userId);
        blog.setIsLike(isMember != null);
    }

    @Override
    public void updateBlogLike(Long id) {
        // 查看当前用户是否点赞
        String userId = UserHolder.getUser().getId().toString();
        String key = RedisConstants.BLOG_LIKED_KEY + id;
        Double isMember = stringRedisTemplate.opsForZSet().score(key, userId);
        if (isMember != null) {
            // 已点赞，取消点赞
            int result = baseMapper.incrLikedById(id, -1);
            if (result > 0) {
                stringRedisTemplate.opsForZSet().remove(key, userId);
            }
        } else {
            // 未点赞，点赞
            int result = baseMapper.incrLikedById(id, 1);
            if (result > 0) {
                stringRedisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());
            }
        }
    }

    @Override
    public List<UserDTO> queryBlogLikes(Long id) {
        // 获取当前博文点赞用户
        String key = RedisConstants.BLOG_LIKED_KEY + id;
        Set<String> range = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if (range == null || range.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> userIds = range.stream().map(Long::valueOf).collect(Collectors.toList());
        List<User> userList = userMapper.listUserByIdsWithOrder(userIds);
        return userList.stream().map(user -> BeanUtil.copyProperties(user, UserDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Long saveBlog(Blog blog) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 保存探店博文
        save(blog);
        // 查询粉丝
        List<Follow> fans = followService.lambdaQuery().eq(Follow::getFollowUserId, user.getId()).list();
        for (Follow fan : fans) {
            String key = RedisConstants.FEED_KEY + fan.getUserId();
            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
        }
        // 返回id
        return blog.getId();
    }

    @Override
    public ScrollResult queryBlogOfFollow(Long max, Integer offset) {
        // 获取当前用户key
        String key = RedisConstants.FEED_KEY + UserHolder.getUser().getId();
        // 查询
        Set<ZSetOperations.TypedTuple<String>> typedTuples =
                stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        // 判断是否为空
        if (typedTuples == null || typedTuples.isEmpty()) {
            return null;
        }
        List<Long> blogIds = new ArrayList<>(typedTuples.size());
        int os = 1;
        long minTime = 0;
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            String blogId = tuple.getValue();
            Double score = tuple.getScore();
            if (blogId == null || score == null) continue;
            blogIds.add(Long.valueOf(blogId));
            long time = score.longValue();
            if (time == minTime) {
                ++os;
            }
            else {
                minTime = time;
                os = 1;
            }
        }
        List<Blog> blogs = baseMapper.selectWithOrderByField(blogIds);
        blogs.forEach(blog -> {
            setBlogIsLiked(blog);
            fillBlogWithUser(blog);
        });
        ScrollResult scrollResult = ScrollResult.builder()
                .list(blogs)
                .minTime(minTime)
                .offset(os)
                .build();
        return scrollResult;
    }
}
