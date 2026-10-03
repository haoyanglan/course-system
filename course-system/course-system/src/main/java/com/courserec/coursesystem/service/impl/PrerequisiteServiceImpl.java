package com.courserec.coursesystem.service.impl;

import com.courserec.coursesystem.entity.Prerequisite;
import com.courserec.coursesystem.mapper.PrerequisiteMapper;
import com.courserec.coursesystem.service.IPrerequisiteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 先修课程依赖表 服务实现类
 * </p>
 *
 * @author Admin
 * @since 2026-03-10
 */
@Service
public class PrerequisiteServiceImpl extends ServiceImpl<PrerequisiteMapper, Prerequisite> implements IPrerequisiteService {

}
