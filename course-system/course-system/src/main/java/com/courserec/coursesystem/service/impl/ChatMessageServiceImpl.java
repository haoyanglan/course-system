package com.courserec.coursesystem.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.courserec.coursesystem.entity.ChatMessage;
import com.courserec.coursesystem.mapper.ChatMessageMapper;
import com.courserec.coursesystem.service.IChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {
}