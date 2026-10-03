package com.courserec.coursesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.courserec.coursesystem.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
}