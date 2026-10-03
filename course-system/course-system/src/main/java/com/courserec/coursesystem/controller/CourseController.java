package com.courserec.coursesystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.common.Result;
import com.courserec.coursesystem.entity.Course;
import com.courserec.coursesystem.entity.SysUser;
import com.courserec.coursesystem.service.ICourseService;
import com.courserec.coursesystem.service.IEnrollmentService;
import com.courserec.coursesystem.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/course")
@CrossOrigin
public class CourseController {

    // 引入课程服务
    @Autowired
    private ICourseService courseService;

    // 【新增 1】：引入用户服务，用来查老师的ID
    @Autowired
    private ISysUserService sysUserService;
    @Autowired private IEnrollmentService enrollmentService;

    // 原本给学生看的选课大厅接口，保持不变
    /**
     * 【终极修复版】：选课大厅列表（强行包含所有新字段，绝不丢失）
     */
    /**
     * 【终极修复版】：选课大厅列表（按发布时间倒序，且带上状态）
     */
    @GetMapping("/list")
    public Result<List<java.util.Map<String, Object>>> getCourseList() {
        // 【核心修改】：按照 ID 倒序排列，最新发布的永远在最上面！
        List<Course> courses = courseService.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Course>().orderByDesc("id"));

        List<java.util.Map<String, Object>> resList = new java.util.ArrayList<>();
        for (Course c : courses) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", c.getId());
            map.put("title", c.getTitle());
            map.put("courseTime", c.getCourseTime());
            map.put("location", c.getLocation());
            map.put("semester", c.getSemester());
            map.put("courseType", c.getCourseType());
            map.put("course_type", c.getCourseType());
            map.put("targetMajor", c.getTargetMajor());
            // 【新增】：透传课程状态 (0或空=进行中，2=已完结)
            map.put("status", c.getStatus());

            if (c.getTeacherId() != null) {
                SysUser teacher = sysUserService.getById(c.getTeacherId());
                map.put("teacher", teacher != null ? teacher.getName() : "未知老师");
            } else {
                map.put("teacher", "暂无");
            }
            resList.add(map);
        }
        return Result.success("获取成功", resList);
    }
    // 【新增 2】：给老师用的专属接口
    @GetMapping("/teacherList")
    public Result<List<Course>> getTeacherCourses(@RequestParam String username) {
        // 1. 去 sys_user 表查这个老师的信息，拿到他的数据库 ID
        QueryWrapper<SysUser> userQuery = new QueryWrapper<>();
        userQuery.eq("username", username);
        SysUser teacher = sysUserService.getOne(userQuery);

        if (teacher == null) {
            // 注意：如果你的 Result 类没有单参数的 error 方法，这里可能会标红。
            // 如果标红了，改成比如 Result.error(500, "未找到该教师信息"); 即可
            return Result.error("未找到该教师信息");
        }

        // 2. 去 course 表查 teacher_id = 老师ID 的所有课
        QueryWrapper<Course> courseQuery = new QueryWrapper<>();
        courseQuery.eq("teacher_id", teacher.getId());
        List<Course> courses = courseService.list(courseQuery);

        // 3. 返回数据给前端表格
        return Result.success("查询成功", courses);
    }
    /**
     * 【新增】：管理员功能 - 添加一门新课程
     */
    @PostMapping("/add")
    public Result<String> addCourse(@RequestBody Course course) {
        System.out.println("====== 开始发布新课程 ======");
        System.out.println("1. 接收到类型：" + course.getCourseType() + " | 专业：" + course.getTargetMajor());

        // 先保存课程，数据库会自动给它分配一个 ID
        courseService.save(course);
        Long newCourseId = course.getId();
        System.out.println("2. 课程已存入数据库，分配的课程ID为：" + newCourseId);

        // 如果是必修课，且指定了专业
        if ("REQUIRED".equals(course.getCourseType()) && course.getTargetMajor() != null && !course.getTargetMajor().trim().isEmpty()) {
            String target = course.getTargetMajor().trim();
            System.out.println("3. 准备为【" + target + "】专业的学生自动排课...");

            // 去用户表里找学生，用 like 防止前后不小心打了空格
            QueryWrapper<SysUser> query = new QueryWrapper<>();
            query.eq("role", "STUDENT").like("major", target);
            List<SysUser> students = sysUserService.list(query);

            System.out.println("4. 找到了 " + students.size() + " 名该专业的学生！");

            // 批量发课
            int count = 0;
            for (SysUser student : students) {
                com.courserec.coursesystem.entity.Enrollment enroll = new com.courserec.coursesystem.entity.Enrollment();
                enroll.setStudentId(student.getId());
                enroll.setCourseId(newCourseId); // 用刚才生成的真实课程ID
                enroll.setStatus((byte) 1); // 1代表选课成功

                enrollmentService.save(enroll);
                count++;
            }
            System.out.println("5. ✅ 成功为 " + count + " 名学生强制插入了必修课表！");
        }
        return Result.success("发布成功", null);
    }
    // 记得在文件最上方引入这个包： import java.util.Collections;

    /**
     * 【新增】：核心功能 - 智能推荐课程
     * 每次调用随机推荐 3 门课（后续如果需要，可以替换成协同过滤等真实算法）
     */
    @GetMapping("/recommend")
    public Result<List<Map<String, Object>>> getRecommendCourses() {
        // 直接调用上面的 getCourseList 方法，拿到最全的数据
        List<Map<String, Object>> allCourses = getCourseList().getData();

        if (allCourses != null && !allCourses.isEmpty()) {
            java.util.Collections.shuffle(allCourses); // 随机打乱
            // 截取前3个作为推荐
            List<Map<String, Object>> recommends = allCourses.size() > 3 ? allCourses.subList(0, 3) : allCourses;
            return Result.success("推荐成功", recommends);
        }
        return Result.success("暂无课程", new ArrayList<>());
    }
    /**
     * 【新增】：修改课程信息接口 (供老师和管理员使用)
     */
    @PostMapping("/update")
    public Result<String> updateCourse(@RequestBody Course course) {
        boolean updated = courseService.updateById(course);
        if (updated) {
            return Result.success("课程信息修改成功！", null);
        }
        return Result.error("修改失败，请稍后重试");
    }
    /**
     * 【新增业务逻辑】：老师一键结课
     */
    @PostMapping("/complete")
    public Result<String> completeCourse(@RequestParam Long id) {
        Course course = courseService.getById(id);
        if(course != null) {
            course.setStatus(2); // 状态 2 代表已完结
            courseService.updateById(course);
        }
        return Result.success("课程已完结", null);
    }// ================= 引入依赖的 Mapper (如果已有可忽略) =================
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.courserec.coursesystem.mapper.CourseMapper courseMapper;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.courserec.coursesystem.mapper.SysUserMapper sysUserMapper;

    // ================= 【核心修复】：课程全景详情接口 =================
    @org.springframework.web.bind.annotation.GetMapping("/detail")
    public com.courserec.coursesystem.common.Result<java.util.Map<String, Object>> getCourseDetail(@org.springframework.web.bind.annotation.RequestParam Long courseId) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();

        try {
            // 1. 查询课程本体信息 (包含刚加的 textbook 和 introduction)
            com.courserec.coursesystem.entity.Course course = courseMapper.selectById(courseId);
            if (course == null) {
                return com.courserec.coursesystem.common.Result.error("该课程不存在或已被删除");
            }
            map.put("course", course);

            // 2. 根据课程绑定的 teacherId，去用户表里查老师的详细资料
            if (course.getTeacherId() != null) {
                com.courserec.coursesystem.entity.SysUser teacher = sysUserMapper.selectById(course.getTeacherId());
                if (teacher != null) {
                    map.put("teacherName", teacher.getName());
                    map.put("teacherIntro", teacher.getIntro());
                    map.put("teacherPhone", teacher.getPhone());
                } else {
                    map.put("teacherName", "未知教师");
                    map.put("teacherIntro", "该教师信息似乎已注销");
                    map.put("teacherPhone", "无");
                }
            }
            return com.courserec.coursesystem.common.Result.success("获取详情成功", map);

        } catch (Exception e) {
            e.printStackTrace();
            return com.courserec.coursesystem.common.Result.error("后端处理详情时发生异常：" + e.getMessage());
        }
    }}