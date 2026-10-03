package com.courserec.coursesystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.common.Result;
import com.courserec.coursesystem.entity.ForumComment;
import com.courserec.coursesystem.entity.ForumPost;
import com.courserec.coursesystem.service.IForumCommentService;
import com.courserec.coursesystem.service.IForumPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin // 别忘了允许跨域！
@RestController
@RequestMapping("/forum")
public class ForumController {

    @Autowired private IForumPostService forumPostService;
    @Autowired private IForumCommentService forumCommentService;

    // 1. 获取帖子列表（支持关键词搜索），并把评论也查出来塞进去
    @GetMapping("/list")
    public Result<List<ForumPost>> getList(@RequestParam(required = false) String keyword) {
        QueryWrapper<ForumPost> query = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 搜索内容或发帖人名字
            query.like("content", keyword).or().like("author_name", keyword);
        }
        query.orderByDesc("create_time"); // 最新发的在最前面

        List<ForumPost> posts = forumPostService.list(query);

        // 遍历每个帖子，去查属于它的评论
        for (ForumPost post : posts) {
            QueryWrapper<ForumComment> cq = new QueryWrapper<>();
            cq.eq("post_id", post.getId()).orderByAsc("create_time");
            post.setComments(forumCommentService.list(cq));
        }
        return Result.success("获取成功", posts);
    }

    // 2. 发帖
    @PostMapping("/add")
    public Result<String> addPost(@RequestBody ForumPost post) {
        post.setCreateTime(LocalDateTime.now());
        post.setLikes(0);
        forumPostService.save(post);
        return Result.success("发布成功", null);
    }

    // 3. 点赞
    @PostMapping("/like")
    public Result<String> likePost(@RequestParam Long id) {
        ForumPost post = forumPostService.getById(id);
        if(post != null) {
            post.setLikes(post.getLikes() + 1);
            forumPostService.updateById(post);
        }
        return Result.success("点赞成功", null);
    }

    // 4. 评论
    @PostMapping("/comment")
    public Result<String> addComment(@RequestBody ForumComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        forumCommentService.save(comment);
        return Result.success("评论成功", null);
    }
    /**
     * 【管理员】：强制删除违规帖子
     */
    @PostMapping("/delete")
    public Result<String> deletePost(@RequestParam Long id) {
        forumPostService.removeById(id);
        // 顺便把这篇帖子下的评论也清空
        forumCommentService.remove(new QueryWrapper<ForumComment>().eq("post_id", id));
        return Result.success("帖子已删除", null);
    }
}