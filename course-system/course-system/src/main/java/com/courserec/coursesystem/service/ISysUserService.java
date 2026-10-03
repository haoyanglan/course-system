package com.courserec.coursesystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.courserec.coursesystem.entity.SysUser;

public interface ISysUserService extends IService<SysUser> {
    // 定义一个登录方法，返回的 String 就是 JWT 令牌
    String login(String username, String password);
}