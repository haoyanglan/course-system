package com.courserec.coursesystem.service;

import com.courserec.coursesystem.entity.Enrollment;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IEnrollmentService extends IService<Enrollment> {

    // 检查这里！必须有这一行，而且是 Long 类型
    String enroll(Long studentId, Long courseId);
}