package com.courserec.coursesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

public class Prerequisite implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Long preCourseId;

    // --- Getter and Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getPreCourseId() { return preCourseId; }
    public void setPreCourseId(Long preCourseId) { this.preCourseId = preCourseId; }
}