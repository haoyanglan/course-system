package com.courserec.coursesystem.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.common.Result;
import com.courserec.coursesystem.entity.ChatMessage;
import com.courserec.coursesystem.entity.SysUser;
import com.courserec.coursesystem.service.IChatMessageService;
import com.courserec.coursesystem.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private IChatMessageService chatMessageService;
    @Autowired
    private ISysUserService sysUserService;

    // 1. 获取通讯录（学生查老师，老师查学生）
    @GetMapping("/contacts")
    public Result<List<SysUser>> getContacts(@RequestParam String role) {
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        // 互看逻辑
        if ("STUDENT".equals(role)) { query.eq("role", "TEACHER"); }
        else { query.eq("role", "STUDENT"); }
        return Result.success("获取成功", sysUserService.list(query));
    }

    // 2. 获取两个人之间的聊天记录
    @GetMapping("/history")
    public Result<List<ChatMessage>> getHistory(@RequestParam String sender, @RequestParam String receiver) {
        QueryWrapper<ChatMessage> query = new QueryWrapper<>();
        // 查找 A发给B 或 B发给A 的消息，并按时间排序
        query.and(q -> q.eq("sender", sender).eq("receiver", receiver)
                        .or().eq("sender", receiver).eq("receiver", sender))
                .orderByAsc("create_time");
        return Result.success("获取成功", chatMessageService.list(query));
    }

    // 3. 发送消息
    // 3. 发送消息
    @PostMapping("/send")
    public Result<String> sendMessage(@RequestBody ChatMessage message) {
        message.setCreateTime(LocalDateTime.now());

        // 【关键修复】：强制告诉数据库，新发的消息状态是 0（未读）！
        message.setIsRead(0);

        chatMessageService.save(message);
        return Result.success("发送成功", null);
    }
    /**
     * 【新增】：获取当前用户的未读消息总数
     */
    @GetMapping("/unreadCount")
    public Result<Long> getUnreadCount(@RequestParam String username) {
        QueryWrapper<ChatMessage> query = new QueryWrapper<>();
        query.eq("receiver", username).eq("is_read", 0);
        return Result.success("获取成功", chatMessageService.count(query));
    }

    /**
     * 【新增】：把某人发给我的消息标记为已读
     */
    /**
     * 【终极修复】：把某人发给我的消息标记为已读（采用最稳妥的逐条更新法）
     */
    @PostMapping("/markRead")
    public Result<String> markRead(@RequestParam String sender, @RequestParam String receiver) {
        // 1. 先把所有别人发给我的、且状态为 0（未读）的消息查出来
        QueryWrapper<ChatMessage> query = new QueryWrapper<>();
        query.eq("sender", sender).eq("receiver", receiver).eq("is_read", 0);

        List<ChatMessage> unreadList = chatMessageService.list(query);

        // 2. 遍历这些消息，挨个把它们标记为 1（已读），并保存到数据库
        for (ChatMessage msg : unreadList) {
            msg.setIsRead(1);
            chatMessageService.updateById(msg);
        }

        return Result.success("标记成功", null);
    }
}