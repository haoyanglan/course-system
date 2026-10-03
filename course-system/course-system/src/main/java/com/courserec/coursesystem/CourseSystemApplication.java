package com.courserec.coursesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 添加下面这一行，告诉 Spring Boot 去哪里找数据库操作类
@MapperScan("com.courserec.coursesystem.mapper")
public class CourseSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseSystemApplication.class, args);
	}
}