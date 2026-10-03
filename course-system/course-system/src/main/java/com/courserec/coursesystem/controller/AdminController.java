package com.courserec.coursesystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.common.Result;
import com.courserec.coursesystem.entity.SysUser;
import com.courserec.coursesystem.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 【绝对可用】：获取所有师生账号
     */
    @GetMapping("/listUsers")
    public Result<List<SysUser>> listUsers() {
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        // 不查管理员自己，按时间倒序
        query.ne("role", "ADMIN").orderByDesc("id");
        return Result.success("获取成功", sysUserService.list(query));
    }

    /**
     * 【绝对可用】：删除账号
     */
    @PostMapping("/deleteUser")
    public Result<String> deleteUser(@RequestParam Long id) {
        sysUserService.removeById(id);
        return Result.success("删除成功", null);
    }
}