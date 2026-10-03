package com.courserec.coursesystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.entity.SysUser;
import com.courserec.coursesystem.service.ISysUserService;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@CrossOrigin
public class AiController {

    private final ChatModel chatModel;

    @Autowired
    private ISysUserService sysUserService;

    public AiController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("msg") String msg, @RequestParam("username") String username) {
        try {
            // 1. 获取当前登录学生的信息
            QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
            userQuery.eq("username", username);
            SysUser student = sysUserService.getOne(userQuery);
            String majorInfo = (student != null && student.getMajor() != null) ? student.getMajor() : "未知专业";

            // 2. 强硬的系统提示词
            String systemPrompt = "你是一个智能教务助手。当前咨询的学生专业是：" + majorInfo + "。\n" +
                    "【最高指令】：\n" +
                    "你当前处于一个封闭的数据库环境中。对于学生提出的任何关于‘有什么课’、‘某门课的时间地点’、‘某门课的学分类型’等问题，你绝对【不能】凭空捏造答案，也【不能】说你不知道。\n" +
                    "你必须、也只能通过调用名为【searchCourseTool】的函数来获取真实的数据库信息。\n" +
                    "如果学生问‘有什么必修课’，你必须立刻调用工具，传入 keyword='必修'。\n" +
                    "在你收到工具返回的数据后，请用亲切、专业的语气进行整理并回答给学生。";

            // 3. 挂载工具
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .withFunction("searchCourseTool")
                    .build();

            // 4. 发送请求
            Prompt prompt = new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(msg)), options);

            return chatModel.call(prompt).getResult().getOutput().getContent();

        } catch (Exception e) {

            e.printStackTrace(); // 后台打印
            return "💥 抓到报错了！\n错误类型：" + e.getClass().getName() + "\n错误详情：" + e.getMessage();
        }
    }
}