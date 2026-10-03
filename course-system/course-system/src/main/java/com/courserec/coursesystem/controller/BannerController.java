package com.courserec.coursesystem.controller;
import com.courserec.coursesystem.entity.SysBanner;
import com.courserec.coursesystem.mapper.SysBannerMapper;
import com.courserec.coursesystem.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/banner")
@CrossOrigin
public class BannerController {

    @Autowired
    private SysBannerMapper bannerMapper;

    @GetMapping("/list")
    public Result<List<SysBanner>> list() {
        return Result.success("获取成功", bannerMapper.selectList(null));
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody SysBanner banner) {
        if (banner.getId() == null) {
            bannerMapper.insert(banner);
        } else {
            bannerMapper.updateById(banner);
        }
        return Result.success("保存成功", null);
    }

    @PostMapping("/delete")
    public Result<String> delete(@RequestParam Long id) {
        bannerMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}