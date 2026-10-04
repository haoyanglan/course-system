package com.courserec.coursesystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.common.Result;
import com.courserec.coursesystem.entity.ForumComment;
import com.courserec.coursesystem.entity.ForumPost;
import com.courserec.coursesystem.service.IForumCommentService;
import com.courserec.coursesystem.service.IForumPostService;
import com.courserec.coursesystem.service.ISysUserService;
import com.courserec.coursesystem.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@CrossOrigin // 别忘了允许跨域！
@RestController
@RequestMapping("/forum")
public class ForumController {

    @Autowired private IForumPostService forumPostService;
    @Autowired private IForumCommentService forumCommentService;
    @Autowired private ISysUserService sysUserService;

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

        if (!posts.isEmpty()) {
            List<Long> ids = posts.stream().map(ForumPost::getId).collect(Collectors.toList());
            List<ForumComment> comments = forumCommentService.list(new QueryWrapper<ForumComment>()
                    .in("post_id", ids).orderByAsc("create_time"));
            Map<Long, List<ForumComment>> byPost = new HashMap<>();
            for (ForumComment comment : comments) {
                byPost.computeIfAbsent(comment.getPostId(), key -> new ArrayList<>()).add(comment);
            }
            for (ForumPost post : posts) post.setComments(byPost.getOrDefault(post.getId(), new ArrayList<>()));
        }
        return Result.success("获取成功", posts);
    }

    // 2. 发帖
    @PostMapping("/add")
    public Result<String> addPost(@RequestBody ForumPost post, @RequestAttribute("authUsername") String authUsername,
                                  @RequestAttribute("authRole") String authRole) {
        if (post.getContent() == null || post.getContent().isBlank()) return Result.error("帖子内容不能为空");
        SysUser author = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", authUsername));
        if (author == null) return Result.error("用户不存在");
        post.setId(null);
        post.setAuthorUsername(authUsername);
        post.setAuthorName(author.getName() + ("ADMIN".equals(authRole) ? " (官方)" : ""));
        post.setCreateTime(LocalDateTime.now());
        post.setLikes(0);
        forumPostService.save(post);
        return Result.success("发布成功", null);
    }

    // 3. 点赞
    @PostMapping("/like")
    public Result<String> likePost(@RequestParam Long id) {
        boolean updated = forumPostService.update(new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<ForumPost>()
                .eq("id", id).setSql("likes = COALESCE(likes, 0) + 1"));
        return updated ? Result.success("点赞成功", null) : Result.error("帖子不存在");
    }

    // 4. 评论
    @PostMapping("/comment")
    public Result<String> addComment(@RequestBody ForumComment comment, @RequestAttribute("authUsername") String authUsername) {
        if (comment.getContent() == null || comment.getContent().isBlank() || comment.getPostId() == null || forumPostService.getById(comment.getPostId()) == null) {
            return Result.error("评论内容或帖子无效");
        }
        SysUser author = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", authUsername));
        if (author == null) return Result.error("用户不存在");
        comment.setId(null);
        comment.setAuthorUsername(authUsername);
        comment.setAuthorName(author.getName());
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
