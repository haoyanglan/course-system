package com.courserec.coursesystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.courserec.coursesystem.entity.Course;
import java.util.List;
import java.util.Map;

public interface ICourseService extends IService<Course> { // 关键：继承 IService
    List<Map<String, Object>> getAllCourses();
}