package com.courserec.coursesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.courserec.coursesystem.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 继承了 BaseMapper，MyBatis-Plus 已经自动帮我们写好了增删改查！
}