package com.hmdp.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.domain.dto.Result;
import com.hmdp.domain.dto.ScrollResult;
import com.hmdp.domain.dto.UserDTO;
import com.hmdp.domain.entity.Blog;
import com.hmdp.service.IBlogService;
import com.hmdp.constant.SystemConstants;
import com.hmdp.util.UserHolder;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private IBlogService blogService;

    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        Long blogId = blogService.saveBlog(blog);
        return Result.success(blogId);
    }

    /**
     * 点赞
     * @param id 博文id
     * @return
     */
    @ApiOperation("点赞博文")
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        // 修改点赞数量
        blogService.updateBlogLike(id);
        return Result.success();
    }

    /**
     * 查询博文点赞用户列表
     * @param id
     * @return
     */
    @ApiOperation("查询博文点赞用户列表")
    @GetMapping("/likes/{id}")
    public Result queryBlogLikes(@PathVariable("id") Long id) {
        List<UserDTO> list = blogService.queryBlogLikes(id);
        return Result.success(list);
    }

    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.success(records);
    }

    @GetMapping("/of/user")
    public Result queryBlogByUserId(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam("id") Long id) {
        // 根据用户查询
        Page<Blog> page = blogService.lambdaQuery()
                .eq(Blog::getUserId, id)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.success(records);
    }

    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        List<Blog> records = blogService.queryHotBlog(current);
        return Result.success(records);
    }

    @ApiOperation("查询博文详情")
    @GetMapping("/{id}")
    public Result queryBlogById(@PathVariable Long id) {
        Blog blog = blogService.queryBlogById(id);
        return Result.success(blog);
    }

    /**
     * 查询关注的更新的，一次看2条，滚动获取
     * @param max
     * @param offset
     * @return
     */
    @ApiOperation("查询关注的更新的博文列表")
    @GetMapping("/of/follow")
    public Result queryBlogOfFollow(
            @RequestParam("lastId") Long max,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset
    ) {
        ScrollResult res = blogService.queryBlogOfFollow(max, offset);
        return Result.success(res);
    }
}
