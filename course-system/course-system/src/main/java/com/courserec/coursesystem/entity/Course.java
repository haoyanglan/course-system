package com.courserec.coursesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String title;
    private Long teacherId;
    private BigDecimal credits;
    private Integer maxCapacity;
    private Integer currentEnrolled;
    private String category;
    private String tags;
    private String description;
    private BigDecimal averageScore;
    private Integer status;
    private String courseTime;
    private String location;
    // 明确告诉JSON和数据库，这个字段叫什么
    @com.baomidou.mybatisplus.annotation.TableField("course_type")
    @com.fasterxml.jackson.annotation.JsonProperty("courseType")
    private String courseType;

    @com.baomidou.mybatisplus.annotation.TableField("target_major")
    @com.fasterxml.jackson.annotation.JsonProperty("targetMajor")
    private String targetMajor;
    @com.baomidou.mybatisplus.annotation.TableField("semester")
    @com.fasterxml.jackson.annotation.JsonProperty("semester")
    private String semester;
    @com.baomidou.mybatisplus.annotation.TableField("introduction")
    private String introduction;

    @com.baomidou.mybatisplus.annotation.TableField("textbook")
    private String textbook;

    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getTextbook() { return textbook; }
    public void setTextbook(String textbook) { this.textbook = textbook; }

    // --- 下面是所有的 Getter 和 Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public BigDecimal getCredits() { return credits; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
    public Integer getCurrentEnrolled() { return currentEnrolled; }
    public void setCurrentEnrolled(Integer currentEnrolled) { this.currentEnrolled = currentEnrolled; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

}