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
 * 用户评分与评价表
 * </p>
 *
 * @author Admin
 * @since 2026-03-09
 */
@Getter
@Setter
@TableName("user_course_rating")
public class UserCourseRating implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评价主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评价用户ID
     */
    private Long userId;

    /**
     * 被评价课程ID
     */
    private Long courseId;

    /**
     * 评分星星数量(1到5星)
     */
    private Byte score;

    /**
     * 文字评价内容(大模型可以对这些评价做情感分析)
     */
    private String comment;

    /**
     * 评价时间
     */
    private LocalDateTime createTime;
}
