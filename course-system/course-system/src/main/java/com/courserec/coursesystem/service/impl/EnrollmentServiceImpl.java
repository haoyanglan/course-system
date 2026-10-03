package com.courserec.coursesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.courserec.coursesystem.entity.*;
import com.courserec.coursesystem.mapper.EnrollmentMapper;
import com.courserec.coursesystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentServiceImpl extends ServiceImpl<EnrollmentMapper, Enrollment> implements IEnrollmentService {

    @Autowired
    private ICourseService courseService;
    @Autowired
    private IPrerequisiteService prerequisiteService;
    @Autowired
    private IScheduleService scheduleService;

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启数据库事务，出错了自动回滚
    public String enroll(Long studentId, Long courseId) {

        // 拦截 1：防重复选课 (你不能同一门课选两次)
        long count = this.count(new QueryWrapper<Enrollment>()
                .eq("student_id", studentId).eq("course_id", courseId));
        if (count > 0) return "操作失败：你已经选过这门课啦！";

        // 拦截 2：容量超卖拦截 (课满没满？)
        Course course = courseService.getById(courseId);
        if (course == null) return "操作失败：课程不存在！";
        if (course.getCurrentEnrolled() >= course.getMaxCapacity()) {
            return "操作失败：手慢了，该课程名额已满！";
        }

        // 拦截 3：先修课程拦截 (比如：没学过数据结构，不能选算法课)
        List<Prerequisite> preReqs = prerequisiteService.list(new QueryWrapper<Prerequisite>().eq("course_id", courseId));
        for (Prerequisite req : preReqs) {
            Enrollment passed = this.getOne(new QueryWrapper<Enrollment>()
                    .eq("student_id", studentId)
                    .eq("course_id", req.getPreCourseId())
                    .ge("final_grade", 60.0)); // 必须及格才算修过
            if (passed == null) {
                return "操作失败：不满足先修课要求，请先修读相关前置课程！";
            }
        }

        // 拦截 4：上课时间冲突拦截 (周几第几节课有没有冲突)
        List<Schedule> targetSchedules = scheduleService.list(new QueryWrapper<Schedule>().eq("course_id", courseId));
        List<Enrollment> myEnrollments = this.list(new QueryWrapper<Enrollment>().eq("student_id", studentId));
        List<Long> myCourseIds = myEnrollments.stream().map(Enrollment::getCourseId).toList();

        if (!myCourseIds.isEmpty() && !targetSchedules.isEmpty()) {
            List<Schedule> mySchedules = scheduleService.list(new QueryWrapper<Schedule>().in("course_id", myCourseIds));
            for (Schedule target : targetSchedules) {
                for (Schedule mine : mySchedules) {
                    if (target.getDayOfWeek().equals(mine.getDayOfWeek())) {
                        // 如果有时间交集，直接拦截
                        if (!(target.getSectionEnd() < mine.getSectionStart() || target.getSectionStart() > mine.getSectionEnd())) {
                            return "操作失败：上课时间冲突！与你已选的课程时间重叠。";
                        }
                    }
                }
            }
        }

        // --- 经过上面四道地狱关卡，终于可以选课了 ---

        // 1. 课程已选人数 + 1
        course.setCurrentEnrolled(course.getCurrentEnrolled() + 1);
        courseService.updateById(course);

        // 2. 写入选课记录表
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudentId(studentId);
        newEnrollment.setCourseId(courseId);
        newEnrollment.setStatus((byte) 1); // 1 表示选课成功
        this.save(newEnrollment);

        return "恭喜！选课成功！";
    }
}