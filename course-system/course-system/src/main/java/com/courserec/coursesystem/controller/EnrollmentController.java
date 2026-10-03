package com.courserec.coursesystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.common.Result;
import com.courserec.coursesystem.entity.Course;
import com.courserec.coursesystem.entity.Enrollment;
import com.courserec.coursesystem.entity.SysUser;
import com.courserec.coursesystem.service.ICourseService;
import com.courserec.coursesystem.service.IEnrollmentService;
import com.courserec.coursesystem.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enroll")
@CrossOrigin
public class EnrollmentController {

    @Autowired
    private IEnrollmentService enrollmentService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ICourseService courseService;
    @Autowired
    private com.courserec.coursesystem.mapper.AttendanceMapper attendanceMapper;

    /**
     * 【新增】：根据学生账号查询他的专属课表
     */
    @GetMapping("/my")
    public Result<List<Course>> getMySchedule(@RequestParam String username) {
        // 1. 先去 sys_user 表查出这个学生是谁（拿到他的数据库 ID）
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.eq("username", username);
        SysUser student = sysUserService.getOne(userQuery);

        if (student == null) {
            return Result.error("找不到该学生信息"); // 如果这里报红，改成 Result.error(500, "找不到")
        }

        // 2. 去选课记录表 (course_enrollment) 查他选了哪些课的 ID
        QueryWrapper<Enrollment> enrollQuery = new QueryWrapper<>();
        // 数据库字段叫 student_id，去找这个学生的记录
        enrollQuery.eq("student_id", student.getId());
        List<Enrollment> enrollments = enrollmentService.list(enrollQuery);

        // 如果他一门课都没选，直接返回一个空的列表
        if (enrollments.isEmpty()) {
            return Result.success("暂无选课", new ArrayList<>());
        }

        // 3. 把他选的那些课程的 ID 单独提取出来存进一个集合里
        List<Long> courseIds = enrollments.stream()
                .map(Enrollment::getCourseId)
                .collect(Collectors.toList());

        // 4. 最后，去课程表 (course) 把这些课程的详细信息查出来返回给前端
        List<Course> myCourses = courseService.listByIds(courseIds);

        return Result.success("查询成功", myCourses);
    }

    /**
     * 【核心功能】：学生提交选课请求
     * 前端点“立即选课”时，会调用这个接口
     */
    @PostMapping("/submit")
    public Result<String> enrollCourse(@RequestParam String username, @RequestParam Long courseId) {
        // 1. 根据用户名（如 S001）查出这个学生的详细信息，拿到他的 ID
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.eq("username", username);
        SysUser student = sysUserService.getOne(userQuery);

        if (student == null) {
            return Result.error("选课失败：学生信息不存在");
        }

        // 2. 【安全检查】：检查这个学生是不是已经选过这门课了
        QueryWrapper<Enrollment> checkQuery = new QueryWrapper<>();
        checkQuery.eq("student_id", student.getId())
                .eq("course_id", courseId);

        long count = enrollmentService.count(checkQuery);
        if (count > 0) {
            return Result.error("你已经选过这门课了，无需重复选择");
        }

        // 3. 【执行选课】：往选课记录表 (course_enrollment) 插入一条新数据
        Enrollment newEnroll = new Enrollment();
        newEnroll.setStudentId(student.getId()); // 设置学生ID
        newEnroll.setCourseId(courseId);        // 设置课程ID

        // 执行保存操作
        boolean saved = enrollmentService.save(newEnroll);

        if (saved) {
            return Result.success("选课成功！", null);
        } else {
            return Result.error("选课失败，请联系管理员");
        }
    }

    /**
     * 【新增】：学生退课功能
     * 前端点“退课”按钮时，会调用这个接口把记录删掉
     */
    @PostMapping("/drop")
    public Result<String> dropCourse(@RequestParam String username, @RequestParam Long courseId) {
        // 先查查这门课是不是必修
        Course course = courseService.getById(courseId);
        if (course != null && "REQUIRED".equals(course.getCourseType())) {
            return Result.error("必修课不可退选！这是学校规定！");
        }

        // （下方保留你原本的退课删除逻辑，假设你原来是通过 username 和 courseId 删除）
        SysUser student = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        QueryWrapper<Enrollment> eq = new QueryWrapper<>();
        eq.eq("student_id", student.getId()).eq("course_id", courseId);
        enrollmentService.remove(eq);
        return Result.success("退课成功", null);
    }

    @PostMapping("/updateGrade")
    public Result<String> updateGrade(@RequestParam Long courseId, @RequestParam Long studentId, @RequestParam Integer grade) {
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Enrollment> update = new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        update.eq("course_id", courseId).eq("student_id", studentId).set("final_grade", grade);
        enrollmentService.update(update);
        return Result.success("成绩已保存", null);
    }
    /**
     * 【新增】：教师端功能 - 根据课程ID，查询选了这门课的所有学生名单
     */
    /**
     * 【修复升级】：获取选课学生名单（完美处理0人选课的情况）
     */
    @GetMapping("/students")
    public Result<List<java.util.Map<String, Object>>> getCourseStudents(@RequestParam Long courseId) {
        List<Enrollment> enrollments = enrollmentService.list(new QueryWrapper<Enrollment>().eq("course_id", courseId));
        if (enrollments == null || enrollments.isEmpty()) {
            return Result.success("目前暂无学生", new java.util.ArrayList<>());
        }

        List<java.util.Map<String, Object>> resList = new java.util.ArrayList<>();
        for (Enrollment e : enrollments) {
            SysUser student = sysUserService.getById(e.getStudentId());
            if (student != null) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("studentId", student.getId()); // 打分要用
                map.put("userNo", student.getUserNo());
                map.put("name", student.getName());
                map.put("major", student.getMajor());
                map.put("className", student.getClassName());
                map.put("grade", e.getFinalGrade()); // 成绩！
                map.put("username", student.getUsername()); // 聊天要用
                resList.add(map);
            }
        }
        return Result.success("获取成功", resList);
    }
    /**
     * 【全新功能】：学生查询自己的成绩（支持按学期过滤）
     */
    /**
     * 【升级版】：学生查询成绩与评教状态
     */
    /**
     * 【终极版】：学生查询成绩与评教状态
     */
    @GetMapping("/myGrades")
    public Result<List<java.util.Map<String, Object>>> getMyGrades(@RequestParam String username, @RequestParam(required = false) String semester) {
        SysUser student = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (student == null) return Result.error("用户不存在");

        List<Enrollment> enrollments = enrollmentService.list(new QueryWrapper<Enrollment>().eq("student_id", student.getId()));
        List<java.util.Map<String, Object>> resList = new java.util.ArrayList<>();

        for (Enrollment e : enrollments) {
            Course c = courseService.getById(e.getCourseId());
            if (c != null) {
                // 学期过滤逻辑
                if (semester != null && !semester.isEmpty() && !"ALL".equals(semester)) {
                    if (!semester.equals(c.getSemester())) continue;
                }

                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("courseId", c.getId()); // 【关键】：用于评教传参
                map.put("courseName", c.getTitle());
                map.put("credits", c.getCredits());
                map.put("courseType", c.getCourseType());
                map.put("semester", c.getSemester());

                // 【核心命脉】：必须把状态传给前端，前端才能判断是不是已结课（2）
                map.put("status", c.getStatus());

                map.put("grade", e.getFinalGrade());
                map.put("evalScore", e.getEvalScore()); // 【关键】：判断是否已评教

                SysUser teacher = sysUserService.getById(c.getTeacherId());
                map.put("teacher", teacher != null ? teacher.getName() : "未知");

                resList.add(map);
            }
        }
        return Result.success("查询成功", resList);
    }

    /**
     * 【全新】：学生提交教学评估
     */
    @PostMapping("/evaluate")
    public Result<String> evaluateCourse(@RequestParam Long courseId, @RequestParam Long studentId, @RequestParam Integer score, @RequestParam String suggestion) {
        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Enrollment> update = new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        update.eq("course_id", courseId).eq("student_id", studentId)
                .set("eval_score", score).set("eval_suggestion", suggestion);
        enrollmentService.update(update);
        return Result.success("评估成功，感谢您的反馈！", null);
    }

    /**
     * 【全新】：老师查看某门课的评教报告
     */
    @GetMapping("/evalStats")
    public Result<java.util.Map<String, Object>> getEvalStats(@RequestParam Long courseId) {
        // 只查那些已经评了分的记录
        List<Enrollment> list = enrollmentService.list(new QueryWrapper<Enrollment>().eq("course_id", courseId).isNotNull("eval_score"));

        java.util.Map<String, Object> res = new java.util.HashMap<>();
        if(list.isEmpty()) {
            res.put("avgScore", "0.0");
            res.put("suggestions", new java.util.ArrayList<>());
            return Result.success("暂无评估数据", res);
        }

        // 计算平均分
        double avg = list.stream().mapToInt(Enrollment::getEvalScore).average().orElse(0.0);
        // 收集所有建议
        java.util.List<String> suggestions = list.stream().map(Enrollment::getEvalSuggestion)
                .filter(s -> s != null && !s.trim().isEmpty()).collect(java.util.stream.Collectors.toList());

        res.put("avgScore", String.format("%.1f", avg)); // 保留一位小数
        res.put("suggestions", suggestions);

        return Result.success("获取成功", res);
    }/**
     * 【教师端】：批量提交本次课的考勤记录
     */
    @PostMapping("/submitAttendance")
    public Result<String> submitAttendance(@RequestBody java.util.List<com.courserec.coursesystem.entity.Attendance> records) {
        for (com.courserec.coursesystem.entity.Attendance a : records) {
            attendanceMapper.insert(a);
        }
        return Result.success("考勤提交成功", null);
    }

    /**
     * 【学生端】：查询自己的考勤记录
     */
    @GetMapping("/myAttendance")
    public Result<java.util.List<java.util.Map<String, Object>>> getMyAttendance(@RequestParam Long studentId) {
        java.util.List<com.courserec.coursesystem.entity.Attendance> list = attendanceMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.courserec.coursesystem.entity.Attendance>()
                        .eq("student_id", studentId).orderByDesc("record_date")
        );
        java.util.List<java.util.Map<String, Object>> res = new java.util.ArrayList<>();
        for(com.courserec.coursesystem.entity.Attendance a : list) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            Course c = courseService.getById(a.getCourseId());
            map.put("courseName", c != null ? c.getTitle() : "未知课程");
            map.put("recordDate", a.getRecordDate());
            map.put("status", a.getStatus());
            res.add(map);
        }
        return Result.success("获取成功", res);
    }}

