package com.courserec.coursesystem.service.impl;

import com.courserec.coursesystem.entity.UserFavorite;
import com.courserec.coursesystem.mapper.UserFavoriteMapper;
import com.courserec.coursesystem.service.IUserFavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户收藏清单 服务实现类
 * </p>
 *
 * @author Admin
 * @since 2026-03-09
 */
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements IUserFavoriteService {

}
