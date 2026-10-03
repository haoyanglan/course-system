package com.courserec.coursesystem.service.impl;

import com.courserec.coursesystem.entity.User;
import com.courserec.coursesystem.mapper.UserMapper;
import com.courserec.coursesystem.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Admin
 * @since 2026-03-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
