package com.courserec.coursesystem.service.impl;

import com.courserec.coursesystem.entity.Schedule;
import com.courserec.coursesystem.mapper.ScheduleMapper;
import com.courserec.coursesystem.service.IScheduleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 排课时间表 服务实现类
 * </p>
 *
 * @author Admin
 * @since 2026-03-10
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements IScheduleService {

}
