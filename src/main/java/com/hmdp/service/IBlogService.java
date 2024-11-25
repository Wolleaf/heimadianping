package com.hmdp.service;

import com.hmdp.dto.ScrollResult;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IBlogService extends IService<Blog> {

    /**
     * 查询热门博客
     * @param current
     * @return
     */
    List<Blog> queryHotBlog(Integer current);

    /**
     * 根据id查询博客
     * @param id
     * @return
     */
    Blog queryBlogById(Long id);

    /**
     * 点赞
     * @param id
     */
    void updateBlogLike(Long id);

    /**
     * 查询博客点赞用户
     * @param id
     * @return
     */
    List<UserDTO> queryBlogLikes(Long id);

    /**
     * 保存博文
     * @param blog
     * @return
     */
    Long saveBlog(Blog blog);

    /**
     * 查询博文
     * @param max
     * @param offset
     * @return
     */
    ScrollResult queryBlogOfFollow(Long max, Integer offset);
}
