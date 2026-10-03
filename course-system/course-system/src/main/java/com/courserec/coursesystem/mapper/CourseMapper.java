package com.courserec.coursesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.courserec.coursesystem.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper extends BaseMapper<Course> { // 关键：继承 BaseMapper

    @Select("SELECT c.id, c.title, t.name AS teacher " +
            "FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id")
    List<Map<String, Object>> selectAllCourses();
}