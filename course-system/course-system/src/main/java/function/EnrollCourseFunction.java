package com.courserec.coursesystem.function;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.entity.Enrollment;
import com.courserec.coursesystem.service.IEnrollmentService;

import java.util.function.Function;

public class EnrollCourseFunction implements Function<EnrollCourseFunction.EnrollRequest, EnrollCourseFunction.EnrollResponse> {

    // 引入真实的数据库选课服务
    private final IEnrollmentService enrollmentService;

    // 构造函数：Spring 启动时会自动把真实的数据库服务塞进来
    public EnrollCourseFunction(IEnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // AI 调用时必须传来的参数：学生ID 和 课程ID
    public record EnrollRequest(Long studentId, Long courseId) {}
    // 我们返回给 AI 的执行结果
    public record EnrollResponse(boolean success, String message) {}

    @Override
    public EnrollResponse apply(EnrollRequest request) {
        System.out.println("🤖 触发大模型真实选课！学生ID: " + request.studentId() + ", 课程ID: " + request.courseId());

        // 1. 检查是否已经选过
        QueryWrapper<Enrollment> checkQuery = new QueryWrapper<>();
        checkQuery.eq("student_id", request.studentId()).eq("course_id", request.courseId());
        if (enrollmentService.count(checkQuery) > 0) {
            return new EnrollResponse(false, "选课失败：该生已经选过这门课了，不能重复选。");
        }

        // 2. 真正写入数据库
        Enrollment newEnroll = new Enrollment();
        newEnroll.setStudentId(request.studentId());
        newEnroll.setCourseId(request.courseId());

        boolean saved = enrollmentService.save(newEnroll);
        if (saved) {
            return new EnrollResponse(true, "选课成功！已经成功写入数据库。");
        } else {
            return new EnrollResponse(false, "数据库写入失败，请检查系统。");
        }
    }
}