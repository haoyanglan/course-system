package com.courserec.coursesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 课程评价反馈表
 * </p>
 *
 * @author Admin
 * @since 2026-03-10
 */
@Getter
@Setter
@TableName("course_rating")
public class Rating implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    /**
     * 综合星级 (1-5)
     */
    private Byte score;

    /**
     * 文字评价 (大模型做词云情感分析的数据源)
     */
    private String comment;

    private LocalDateTime createTime;
}
