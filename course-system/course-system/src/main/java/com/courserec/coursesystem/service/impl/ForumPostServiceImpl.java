package com.courserec.coursesystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.courserec.coursesystem.entity.ForumPost;
import com.courserec.coursesystem.mapper.ForumPostMapper;
import com.courserec.coursesystem.service.IForumPostService;
import org.springframework.stereotype.Service;

@Service
public class ForumPostServiceImpl extends ServiceImpl<ForumPostMapper, ForumPost> implements IForumPostService {
}