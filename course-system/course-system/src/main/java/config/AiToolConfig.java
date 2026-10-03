package com.courserec.coursesystem.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.entity.Course;
import com.courserec.coursesystem.service.ICourseService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AiToolConfig {

    @Resource
    private ICourseService courseService;

    // 【核心修复区】：加上 Jackson 注解，生成 DeepSeek 绝对能看懂的 JSON Schema
    public record CourseSearchRequest(
            @JsonProperty(required = true, value = "keyword")
            @JsonPropertyDescription("必填。用于搜索课程的关键词，例如：必修、选修、软件工程、操作系统等")
            String keyword
    ) {}

    @Bean
    @Description("这是一个查询教务系统底层数据库的工具。只要学生问到任何关于课程的问题，必须调用此工具查询实时数据。")
    public Function<CourseSearchRequest, String> searchCourseTool() {
        return request -> {
            System.out.println("====== 恭喜！DeepSeek 成功调用查库工具！搜索词：" + request.keyword() + " ======");

            QueryWrapper<Course> query = new QueryWrapper<>();
            if (request.keyword() != null && !request.keyword().isBlank()) {
                query.like("title", request.keyword())
                        .or()
                        .like("course_type", request.keyword());
            }
            query.last("LIMIT 10");

            List<Course> courses = courseService.list(query);

            if (courses == null || courses.isEmpty()) {
                return "没有查找到匹配的课程。";
            }

            return courses.stream()
                    .map(c -> "课程名:" + c.getTitle() + ", 类型:" + c.getCourseType() + ", 时间:" + c.getCourseTime() + ", 地点:" + c.getLocation())
                    .collect(Collectors.joining("；\n"));
        };
    }
}