package com.courserec.coursesystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.courserec.coursesystem.entity.SysUser;
import com.courserec.coursesystem.mapper.SysUserMapper;
import com.courserec.coursesystem.service.ISysUserService;
import com.courserec.coursesystem.utils.JwtUtils;
import com.courserec.coursesystem.utils.PasswordUtils;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Override
    public String login(String username, String password) {
        // 只按账号查询，兼容旧明文数据和新的 BCrypt 密码。
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        SysUser user = this.getOne(queryWrapper);

        // 2. 如果没找到，说明账号或密码错误
        if (user == null || !PasswordUtils.matches(password, user.getPassword())) {
            throw new RuntimeException("账号或密码错误！");
        }

        if (!PasswordUtils.isHashed(user.getPassword())) {
            user.setPassword(PasswordUtils.hash(password));
            this.updateById(user);
        }

        // 3. 如果找到了，用我们写好的 JwtUtils 给他颁发一张包含他账号和角色的 Token 通行证！
        return JwtUtils.generateToken(user.getUsername(), user.getRole());
    }
}
