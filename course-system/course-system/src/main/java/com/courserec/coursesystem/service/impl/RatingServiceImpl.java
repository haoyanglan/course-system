package com.courserec.coursesystem.service.impl;

import com.courserec.coursesystem.entity.Rating;
import com.courserec.coursesystem.mapper.RatingMapper;
import com.courserec.coursesystem.service.IRatingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程评价反馈表 服务实现类
 * </p>
 *
 * @author Admin
 * @since 2026-03-10
 */
@Service
public class RatingServiceImpl extends ServiceImpl<RatingMapper, Rating> implements IRatingService {

}
