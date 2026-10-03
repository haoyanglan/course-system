package com.courserec.coursesystem.controller;

import com.courserec.coursesystem.common.Result;
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
    public Result<String> reserveClassroom(@RequestBody Map<String, Object> map) {
        jdbcTemplate.update("INSERT INTO classroom_reservation (room_name, applicant_name, reserve_date, time_slot, purpose) VALUES (?, ?, ?, ?, ?)",
                map.get("roomName"), map.get("applicantName"), map.get("reserveDate"), map.get("timeSlot"), map.get("purpose"));
        return Result.success("预约成功！", null);
    }

    @GetMapping("/myReservations")
    public Result<List<Map<String, Object>>> getMyReservations(@RequestParam String applicantName) {
        return Result.success("获取成功", jdbcTemplate.queryForList(
                "SELECT * FROM classroom_reservation WHERE applicant_name = ? ORDER BY id DESC", applicantName));
    }
}