-- 示例初始数据（可选）
-- 使用说明：请先执行 schema.sql 建表，再执行本文件插入演示数据。
-- 注意：本项目为演示用途，密码为明文存储；正式部署前务必改为加密存储并修改默认密码。

USE courserec_db;

-- 默认账号：admin / admin123（管理员），teacher01 / 123456（教师），student01 / 123456（学生）
INSERT INTO `sys_user` (`username`, `password`, `name`, `role`, `user_no`, `phone`, `intro`, `major`, `class_name`) VALUES
('admin', 'admin123', '系统管理员', 'ADMIN', 'A0001', '13800000000', NULL, NULL, NULL),
('teacher01', '123456', '张老师', 'TEACHER', 'T1001', '13800000001', '主讲软件工程相关课程，十年教学经验。', '软件工程', NULL),
('student01', '123456', '李同学', 'STUDENT', 'S2001', '13900000001', NULL, '软件工程', '软工2201班');

INSERT INTO `sys_classroom` (`room_name`, `capacity`) VALUES
('教一101', 60),
('教一102', 120),
('实验楼201', 80),
('多媒体报告厅', 300);

INSERT INTO `course` (`title`, `teacher_id`, `credits`, `max_capacity`, `current_enrolled`, `category`, `tags`, `description`, `average_score`, `status`, `course_time`, `location`, `course_type`, `target_major`, `semester`, `introduction`, `textbook`) VALUES
('数据结构与算法', 2, 4.0, 100, 0, '必修', '算法,基础', '线性表、树、图等核心数据结构与常用算法。', 4.2, 1, '2-16周 周一 1-2节', '教一101', 'REQUIRED', '软件工程', '2025-2026春季', '面向软件工程专业的核心基础课程。', '《数据结构（C语言版）》'),
('人工智能大模型应用实战', 2, 2.0, 50, 0, '选修', 'AI,大模型', '大模型基础原理与工程化应用实战。', 4.5, 1, '4-12周 周三 5-6节', '实验楼201', 'ELECTIVE', NULL, '2025-2026春季', '从提示词到 RAG 与 Agent 的动手实践课程。', '无指定教材'),
('前端 Vue3 高级实战', 2, 3.0, 60, 0, '选修', '前端,Vue', 'Vue3 组合式 API、生态与工程化实战。', 4.0, 1, '1-14周 周五 5-6节', '教一102', 'ELECTIVE', NULL, '2025-2026春季', '通过真实项目掌握现代前端开发。', '无指定教材');
