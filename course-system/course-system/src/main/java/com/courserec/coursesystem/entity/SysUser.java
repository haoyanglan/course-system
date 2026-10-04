package com.courserec.coursesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;

@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String name;
    private String role;

    @TableField("user_no")
    private String userNo;

    private String phone;
    private String intro;

    // 【核心修复】：绑定专业和班级
    @TableField("major")
    private String major;

    @TableField("class_name")
    private String className;

    // ====== Get 和 Set ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUserNo() { return userNo; }
    public void setUserNo(String userNo) { this.userNo = userNo; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
