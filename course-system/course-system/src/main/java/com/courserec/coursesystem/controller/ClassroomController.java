package com.courserec.coursesystem.controller;

import com.courserec.coursesystem.common.Result;
import com.courserec.coursesystem.entity.SysUser;
import com.courserec.coursesystem.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classroom")
@CrossOrigin
public class ClassroomController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ISysUserService sysUserService;

    // ================= 管理员：管理物理教室 =================
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listClassrooms() {
        return Result.success("获取成功", jdbcTemplate.queryForList("SELECT * FROM sys_classroom ORDER BY id DESC"));
    }

    @PostMapping("/add")
    public Result<String> addClassroom(@RequestBody Map<String, Object> map) {
        jdbcTemplate.update("INSERT INTO sys_classroom (room_name, capacity) VALUES (?, ?)",
                map.get("roomName"), map.get("capacity"));
        return Result.success("添加成功", null);
    }

    @PostMapping("/delete")
    public Result<String> deleteClassroom(@RequestParam Long id) {
        jdbcTemplate.update("DELETE FROM sys_classroom WHERE id = ?", id);
        return Result.success("删除成功", null);
    }

    // ================= 师生：查空教室与预约 =================
    @GetMapping("/emptyList")
    public Result<List<Map<String, Object>>> getEmptyClassrooms(@RequestParam String date, @RequestParam String slot) {
        // 核心算法：查出所有教室中，没有在指定日期和时间段被预约的教室
        String sql = "SELECT * FROM sys_classroom WHERE room_name NOT IN " +
                "(SELECT room_name FROM classroom_reservation WHERE reserve_date = ? AND time_slot = ?)";
        return Result.success("查询成功", jdbcTemplate.queryForList(sql, date, slot));
    }

    @PostMapping("/reserve")
    public Result<String> reserveClassroom(@RequestBody Map<String, Object> map,
                                           @RequestAttribute("authUsername") String authUsername) {
        SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", authUsername));
        if (user == null) return Result.error("用户不存在");
        if (map.get("roomName") == null || map.get("reserveDate") == null || map.get("timeSlot") == null
                || map.get("purpose") == null || map.get("purpose").toString().isBlank()) return Result.error("请填写完整预约信息");
        Long existing = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM classroom_reservation WHERE room_name = ? AND reserve_date = ? AND time_slot = ?",
                Long.class, map.get("roomName"), map.get("reserveDate"), map.get("timeSlot"));
        if (existing != null && existing > 0) return Result.error("该时段已被预约，请重新查询空教室");
        jdbcTemplate.update("INSERT INTO classroom_reservation (room_name, applicant_name, reserve_date, time_slot, purpose) VALUES (?, ?, ?, ?, ?)",
                map.get("roomName"), user.getName(), map.get("reserveDate"), map.get("timeSlot"), map.get("purpose"));
        return Result.success("预约成功！", null);
    }

    @GetMapping("/myReservations")
    public Result<List<Map<String, Object>>> getMyReservations(@RequestParam String applicantName,
                                                                 @RequestAttribute("authUsername") String authUsername) {
        SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", authUsername));
        if (user == null) return Result.error("用户不存在");
        return Result.success("获取成功", jdbcTemplate.queryForList(
                "SELECT * FROM classroom_reservation WHERE applicant_name = ? ORDER BY id DESC", user.getName()));
    }
}
