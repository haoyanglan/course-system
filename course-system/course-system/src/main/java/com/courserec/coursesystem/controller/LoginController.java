package com.courserec.coursesystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.courserec.coursesystem.common.Result;
import com.courserec.coursesystem.entity.SysUser;
import com.courserec.coursesystem.service.ISysUserService;
import com.courserec.coursesystem.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class LoginController {

    @Autowired
    private ISysUserService sysUserService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody SysUser sysUser) {
        try {
            // 调用 Service 层的登录逻辑
            String token = sysUserService.login(sysUser.getUsername(), sysUser.getPassword());
            // 登录成功，把 Token 包装在 Result 里发给前端
            return Result.success("登录成功", token);
        } catch (Exception e) {
            // 登录失败（比如密码错误），返回错误信息
            Result<String> errorResult = new Result<>();
            errorResult.setCode(500);
            errorResult.setMessage(e.getMessage());
            return errorResult;
        }
    }
    /**
     * 【新增】：用户注册接口（支持学生和教师）
     * 接收前端传来的 JSON 格式注册信息
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUser user) {
        if (user.getUsername() == null || user.getUsername().isBlank() || user.getName() == null || user.getName().isBlank()) {
            return Result.error("请填写账号和姓名");
        }
        if (!"STUDENT".equals(user.getRole()) && !"TEACHER".equals(user.getRole())) {
            return Result.error("只能注册学生或教师账号");
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            return Result.error("密码至少需要 8 位");
        }
        // 1. 检查账号是否已经被别人注册了
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());

        // 注意：如果你这行 sysUserService 报错报红，说明你当前文件没有引入它。
        // 你需要在文件上面加一行：@Autowired private ISysUserService sysUserService;
        if (sysUserService.count(queryWrapper) > 0) {
            return Result.error("注册失败：该账号已被占用，请换一个账号名");
        }

        // 2. 将包含学号/工号、联系方式、教师介绍的完整信息存入数据库
        user.setId(null);
        user.setPassword(PasswordUtils.hash(user.getPassword()));
        boolean saved = sysUserService.save(user);

        if (saved) {
            return Result.success("注册成功！", null);
        } else {
            return Result.error("注册失败，请稍后重试");
        }
    }
    /**
     * 【新增】：忘记密码功能
     * 根据账号和预留的联系方式重置密码
     */
    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@RequestBody java.util.Map<String, String> params) {
        String username = params.get("username");
        String phone = params.get("phone");
        String newPassword = params.get("newPassword");
        if (username == null || username.isBlank() || phone == null || phone.isBlank() || newPassword == null || newPassword.length() < 8) {
            return Result.error("请填写完整信息，新密码至少 8 位");
        }

        // 1. 根据填写的账号和联系方式，去数据库里找人对暗号
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("phone", phone);
        SysUser user = sysUserService.getOne(queryWrapper);

        if (user == null) {
            return Result.error("重置失败：账号不存在，或预留的联系方式不匹配！");
        }

        // 2. 暗号对上了，给他换成新密码
        user.setPassword(PasswordUtils.hash(newPassword));
        // updateById 是 MyBatis-Plus 自带的更新功能
        boolean updated = sysUserService.updateById(user);

        if (updated) {
            return Result.success("密码重置成功！请使用新密码登录。", null);
        } else {
            return Result.error("系统繁忙，密码重置失败");
        }
    }
    /**
     * 【新增】：获取当前登录用户的详细信息
     */
    @GetMapping("/getUserInfo")
    public Result<SysUser> getUserInfo(@RequestParam String username, @RequestAttribute("authUsername") String authUsername) {
        if (!authUsername.equals(username)) return Result.error("只能查看自己的资料");
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        SysUser user = sysUserService.getOne(queryWrapper);
        if (user != null) {
            return Result.success("获取成功", user);
        }
        return Result.error("找不到该用户");
    }

    /**
     * 【新增】：保存个人中心的修改
     */
    @PostMapping("/updateProfile")
    public Result<String> updateProfile(@RequestBody SysUser user, @RequestAttribute("authUsername") String authUsername) {
        SysUser current = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", authUsername));
        if (current == null || user.getId() == null || !current.getId().equals(user.getId())) {
            return Result.error("只能修改自己的资料");
        }
        if (user.getPassword() != null && !user.getPassword().isBlank() && user.getPassword().length() < 8) {
            return Result.error("新密码至少需要 8 位");
        }
        current.setName(user.getName());
        current.setPhone(user.getPhone());
        current.setIntro(user.getIntro());
        current.setMajor(user.getMajor());
        current.setClassName(user.getClassName());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            current.setPassword(PasswordUtils.hash(user.getPassword()));
        }
        boolean updated = sysUserService.updateById(current);
        if (updated) {
            return Result.success("个人信息修改成功！", null);
        } else {
            return Result.error("修改失败，请检查数据");
        }
    }
}
