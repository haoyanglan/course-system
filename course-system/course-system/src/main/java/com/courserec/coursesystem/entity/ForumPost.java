package com.courserec.coursesystem.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.List;

public class ForumPost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String authorUsername;
    private String authorName;
    private String content;
    private String imageBase64;
    private Integer likes;
    private LocalDateTime createTime;

    // 【重要】：这个字段不存在于数据库表中，仅用于后端往前端塞评论数据
    @TableField(exist = false)
    private List<ForumComment> comments;

    // 生成的 Get 和 Set
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public List<ForumComment> getComments() { return comments; }
    public void setComments(List<ForumComment> comments) { this.comments = comments; }
}