package com.courserec.coursesystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.courserec.coursesystem.entity.ForumComment;
import com.courserec.coursesystem.mapper.ForumCommentMapper;
import com.courserec.coursesystem.service.IForumCommentService;
import org.springframework.stereotype.Service;

@Service
public class ForumCommentServiceImpl extends ServiceImpl<ForumCommentMapper, ForumComment> implements IForumCommentService {
}