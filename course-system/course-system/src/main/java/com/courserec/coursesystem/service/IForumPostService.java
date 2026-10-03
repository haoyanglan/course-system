package com.courserec.coursesystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.courserec.coursesystem.entity.ForumPost;

// 【关键】：必须 extends IService，才能拥有 save, list 等超能力
public interface IForumPostService extends IService<ForumPost> {
}