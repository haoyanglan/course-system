package com.courserec.coursesystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.courserec.coursesystem.entity.Course;
import com.courserec.coursesystem.mapper.CourseMapper;
import com.courserec.coursesystem.service.ICourseService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService { // 关键：继承 ServiceImpl

    @Override
    public List<Map<String, Object>> getAllCourses() {
        // 使用 MyBatis-Plus 提供的 baseMapper 来调用我们自己写的 SQL
        return this.baseMapper.selectAllCourses();
    }
}