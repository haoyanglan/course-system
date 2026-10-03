package com.courserec.coursesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 选课与成绩记录表
 * </p>
 *
 * @author Admin
 * @since 2026-03-10
 */
@Getter
@Setter
@TableName("course_enrollment")
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 状态: 0-候补队列, 1-选课成功, 2-已退课
     */
    private Byte status;

    /**
     * 最终期末成绩
     */
    private BigDecimal finalGrade;

    /**
     * 选课操作时间
     */
    private LocalDateTime createTime;
    @com.baomidou.mybatisplus.annotation.TableField("eval_score")
    private Integer evalScore;

    @com.baomidou.mybatisplus.annotation.TableField("eval_suggestion")
    private String evalSuggestion;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
    public Integer getEvalScore() { return evalScore; }
    public void setEvalScore(Integer evalScore) { this.evalScore = evalScore; }
    public String getEvalSuggestion() { return evalSuggestion; }
    public void setEvalSuggestion(String evalSuggestion) { this.evalSuggestion = evalSuggestion; }
}
