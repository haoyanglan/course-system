package com.courserec.coursesystem;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");

        // 1. 数据库配置：通过环境变量注入，避免把密码写进代码/仓库
        String url = System.getenv().getOrDefault("DB_URL",
                "jdbc:mysql://localhost:3306/courserec_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai");
        String username = System.getenv().getOrDefault("DB_USERNAME", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");

        FastAutoGenerator.create(url, username, password)

                // 2. 全局配置
                .globalConfig(builder -> {
                    builder.author("Admin")
                            .outputDir(projectPath + "/src/main/java");
                })

                // 3. 包配置
                .packageConfig(builder -> {
                    builder.parent("com.courserec.coursesystem")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/src/main/resources/mapper"));
                })

                // 4. 策略配置 (核心修改点在此 👇)
                .strategyConfig(builder -> {
                    // 填入我们今天新建的 6 张核心业务表
                    builder.addInclude("sys_user", "course", "course_schedule", "course_enrollment", "course_prerequisite", "course_rating")
                            // 自动过滤掉表名的前缀，生成的类名会非常清爽 (比如 course_enrollment -> Enrollment)
                            .addTablePrefix("sys_", "course_")
                            .entityBuilder()
                            .enableLombok()
                            .controllerBuilder()
                            .enableRestStyle();
                })

                // 5. 模板引擎配置
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
