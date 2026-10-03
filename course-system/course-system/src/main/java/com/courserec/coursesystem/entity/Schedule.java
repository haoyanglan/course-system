package com.courserec.coursesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private String classroom;
    private Byte dayOfWeek;
    private Byte sectionStart;
    private Byte sectionEnd;

    // --- Getter and Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }
    public Byte getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Byte dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public Byte getSectionStart() { return sectionStart; }
    public void setSectionStart(Byte sectionStart) { this.sectionStart = sectionStart; }
    public Byte getSectionEnd() { return sectionEnd; }
    public void setSectionEnd(Byte sectionEnd) { this.sectionEnd = sectionEnd; }
}