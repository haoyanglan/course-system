
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送者账号',
  `receiver` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收者账号',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `is_read` int DEFAULT '0' COMMENT '0未读 1已读',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `classroom_reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classroom_reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '教室名称',
  `applicant_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '申请人姓名',
  `reserve_date` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预约日期(如: 2026-05-01)',
  `time_slot` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时间段(如: 上午1)',
  `purpose` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '使用用途',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '授课教师ID (关联 sys_user)',
  `credits` decimal(2,1) NOT NULL COMMENT '课程学分 (如: 2.0)',
  `max_capacity` int NOT NULL COMMENT '最大选课人数容量 (用于超卖与候补逻辑)',
  `current_enrolled` int DEFAULT '0' COMMENT '当前已选人数 (可配合 Redis 乐观锁更新)',
  `category` varchar(50) DEFAULT NULL COMMENT '课程类别 (如: 必修, 选修, 人文)',
  `tags` varchar(255) DEFAULT NULL COMMENT 'AI 推荐标签词',
  `description` text COMMENT '大纲描述 (供大模型读取进行语义分析)',
  `average_score` decimal(3,1) DEFAULT '0.0' COMMENT '历史平均分 (用于选课难度预警)',
  `status` tinyint DEFAULT '1' COMMENT '状态: 0-未审核, 1-可正常选课, 2-已结课',
  `course_time` varchar(100) DEFAULT NULL COMMENT '上课时间(如:周一 8:00-10:00)',
  `location` varchar(100) DEFAULT NULL COMMENT '上课地点(如:教一201)',
  `course_type` varchar(20) DEFAULT 'ELECTIVE' COMMENT 'REQUIRED:必修, ELECTIVE:选修',
  `target_major` varchar(50) DEFAULT NULL COMMENT '必修课针对的专业(选修课留空)',
  `semester` varchar(50) DEFAULT '2025-2026春季' COMMENT '开课学期',
  `introduction` text COMMENT '课程介绍',
  `textbook` varchar(255) DEFAULT NULL COMMENT '指定教材',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程信息表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `course_attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `student_id` bigint DEFAULT NULL COMMENT '学生ID',
  `record_date` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '考勤日期(如:2025-10-25)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '考勤状态: PRESENT(正常), ABSENT(缺勤), LATE(迟到), LEAVE(请假)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `course_enrollment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_enrollment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 0-候补队列, 1-选课成功, 2-已退课',
  `final_grade` decimal(4,1) DEFAULT NULL COMMENT '最终期末成绩',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '选课操作时间',
  `eval_score` int DEFAULT NULL COMMENT '评教总分',
  `eval_suggestion` text COMMENT '匿名改进建议',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_course` (`student_id`,`course_id`) COMMENT '防止重复选同一门课'
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='选课与成绩记录表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `course_prerequisite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_prerequisite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '目标课程ID (如: 算法高级实践)',
  `pre_course_id` bigint NOT NULL COMMENT '必须先修的课程ID (如: 数据结构)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_pre` (`course_id`,`pre_course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='先修课程依赖表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `course_rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_rating` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `score` tinyint NOT NULL COMMENT '综合星级 (1-5)',
  `comment` varchar(500) DEFAULT NULL COMMENT '文字评价 (大模型做词云情感分析的数据源)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_course_rating` (`student_id`,`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程评价反馈表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `course_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `classroom` varchar(50) NOT NULL COMMENT '上课教室',
  `day_of_week` tinyint NOT NULL COMMENT '星期几 (1-7)',
  `section_start` tinyint NOT NULL COMMENT '开始节次 (如: 第1节)',
  `section_end` tinyint NOT NULL COMMENT '结束节次 (如: 第2节)',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='排课时间表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `forum_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '关联的帖子ID',
  `author_username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论人账号',
  `author_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评论人姓名',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `forum_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `author_username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发帖人账号',
  `author_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发帖人姓名',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子文字内容',
  `image_base64` longtext COLLATE utf8mb4_unicode_ci COMMENT '图片Base64编码',
  `likes` int DEFAULT '0' COMMENT '点赞数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `prerequisite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prerequisite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '当前课程ID',
  `pre_course_id` bigint NOT NULL COMMENT '必须先修的课程ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `classroom` varchar(50) DEFAULT NULL COMMENT '上课教室',
  `day_of_week` int NOT NULL COMMENT '星期几(1-7)',
  `section_start` int NOT NULL COMMENT '开始节次(如第1节)',
  `section_end` int NOT NULL COMMENT '结束节次(如第2节)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_banner` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主标题',
  `sub` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '副标题',
  `image_base64` longtext COLLATE utf8mb4_unicode_ci COMMENT '图片Base64数据',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_classroom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_classroom` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '教室名称(如: 教一201)',
  `capacity` int DEFAULT NULL COMMENT '容纳人数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '登录账号(学号/工号/admin)',
  `password` varchar(100) NOT NULL COMMENT '登录密码',
  `name` varchar(50) NOT NULL COMMENT '真实姓名',
  `role` varchar(20) NOT NULL COMMENT '角色标识: ADMIN(管理员), TEACHER(教师), STUDENT(学生)',
  `user_no` varchar(50) DEFAULT NULL COMMENT '学号或工号',
  `phone` varchar(50) DEFAULT NULL COMMENT '联系方式',
  `intro` text COMMENT '教师介绍(学生为空)',
  `major` varchar(50) DEFAULT NULL COMMENT '专业，例如：软件工程',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级，例如：软工2401班',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '教师姓名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

