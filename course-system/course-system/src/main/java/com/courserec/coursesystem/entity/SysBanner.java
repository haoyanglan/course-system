package com.courserec.coursesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

@TableName("sys_banner")
public class SysBanner {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String sub;

    // 【核心修改】：绑定为图片字段
    @TableField("image_base64")
    private String imageBase64;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSub() { return sub; }
    public void setSub(String sub) { this.sub = sub; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}