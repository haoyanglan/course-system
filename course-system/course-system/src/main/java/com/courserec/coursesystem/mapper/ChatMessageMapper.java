package com.courserec.coursesystem.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.courserec.coursesystem.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}