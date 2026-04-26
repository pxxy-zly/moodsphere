-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: moodsphere
-- ------------------------------------------------------
-- Server version	8.0.36

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

--
-- Table structure for table `biz_ai_analysis_result`
--

DROP TABLE IF EXISTS `biz_ai_analysis_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_ai_analysis_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `primary_emotion` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主情绪',
  `secondary_emotion` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '次情绪',
  `emotion_keywords` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '情绪关键词，逗号分隔',
  `emotion_scores` text COLLATE utf8mb4_general_ci COMMENT '情绪维度得分JSON',
  `scene_recognition` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '识别场景',
  `risk_level` tinyint DEFAULT '0' COMMENT '风险等级（0无 1低 2中 3高）',
  `risk_reason` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '风险原因',
  `ai_summary` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'AI摘要',
  `raw_response` longtext COLLATE utf8mb4_general_ci COMMENT 'AI原始返回',
  `provider` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模型供应商',
  `model_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模型名称',
  `model_version` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模型版本',
  `prompt_version` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Prompt版本',
  `request_id` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求链路ID',
  `analysis_at` datetime DEFAULT NULL COMMENT '分析完成时间',
  `analysis_cost_ms` int DEFAULT NULL COMMENT '分析耗时毫秒',
  `status` tinyint DEFAULT '1' COMMENT '状态（0失败 1成功）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_primary_emotion` (`primary_emotion`),
  KEY `idx_risk_level` (`risk_level`),
  KEY `idx_request_id` (`request_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI分析结果表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_ai_analysis_result`
--

LOCK TABLES `biz_ai_analysis_result` WRITE;
/*!40000 ALTER TABLE `biz_ai_analysis_result` DISABLE KEYS */;
INSERT INTO `biz_ai_analysis_result` VALUES (1,1,'happy','calm','今天很开心','{\"happy\":0.75,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.48,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.13,\"irritable\":0.13,\"sad\":0.13,\"warm\":0.13,\"confused\":0.13,\"hopeful\":0.13}','general',0,'积极情绪','主情绪为happy，次情绪为calm。 关键词：今天很开心。 整体情绪风险较低。','{\"mode\":\"mock-rule\",\"primaryEmotion\":\"happy\",\"secondaryEmotion\":\"calm\",\"riskLevel\":0,\"scene\":\"general\",\"keywords\":\"今天很开心\",\"scores\":{\"happy\":0.75,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.48,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.13,\"irritable\":0.13,\"sad\":0.13,\"warm\":0.13,\"confused\":0.13,\"hopeful\":0.13}}','mock-provider','rule-engine-v1','1.0.0','p0-mock-v1','55c054aaa46441169f2d0e1c20d491ed','2026-03-17 22:03:11',5,1,'2026-03-17 22:03:11','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-17 22:03:11','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(2,2,'sad','lonely','今天很伤心','{\"happy\":0.13,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.13,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.48,\"irritable\":0.13,\"sad\":0.75,\"warm\":0.13,\"confused\":0.13,\"hopeful\":0.13}','general',1,'检测到低落倾向','主情绪为sad，次情绪为lonely。 关键词：今天很伤心。 检测到潜在风险信号，建议及时关注自身状态并寻求帮助。','{\"mode\":\"mock-rule\",\"primaryEmotion\":\"sad\",\"secondaryEmotion\":\"lonely\",\"riskLevel\":1,\"scene\":\"general\",\"keywords\":\"今天很伤心\",\"scores\":{\"happy\":0.13,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.13,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.48,\"irritable\":0.13,\"sad\":0.75,\"warm\":0.13,\"confused\":0.13,\"hopeful\":0.13}}','mock-provider','rule-engine-v1','1.0.0','p0-mock-v1','efa4e45fa2a84732ab1e974f0876e827','2026-03-17 22:03:38',1,1,'2026-03-17 22:03:38','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-17 22:03:38','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(3,3,'happy','calm','开心、学习','{\"happy\":0.75,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.48,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.13,\"irritable\":0.13,\"sad\":0.13,\"warm\":0.13,\"confused\":0.13,\"hopeful\":0.13}','study',0,'积极情绪','主情绪为happy，次情绪为calm。 关键词：开心、学习。 整体情绪风险较低。','{\"mode\":\"mock-rule\",\"primaryEmotion\":\"happy\",\"secondaryEmotion\":\"calm\",\"riskLevel\":0,\"scene\":\"study\",\"keywords\":\"开心、学习\",\"scores\":{\"happy\":0.75,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.48,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.13,\"irritable\":0.13,\"sad\":0.13,\"warm\":0.13,\"confused\":0.13,\"hopeful\":0.13}}','mock-provider','rule-engine-v1','1.0.0','p0-mock-v1','6c7f9ac087814bdda945ffe8a56fdec2','2026-03-29 22:11:20',6,1,'2026-03-29 22:11:20','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:11:20','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(4,4,'happy','calm','开心、学习','{\"happy\":0.75,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.48,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.13,\"irritable\":0.13,\"sad\":0.13,\"warm\":0.13,\"confused\":0.13,\"hopeful\":0.13}','study',0,'积极情绪','主情绪为happy，次情绪为calm。 关键词：开心、学习。 整体情绪风险较低。','{\"mode\":\"mock-rule\",\"primaryEmotion\":\"happy\",\"secondaryEmotion\":\"calm\",\"riskLevel\":0,\"scene\":\"study\",\"keywords\":\"开心、学习\",\"scores\":{\"happy\":0.75,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.48,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.13,\"irritable\":0.13,\"sad\":0.13,\"warm\":0.13,\"confused\":0.13,\"hopeful\":0.13}}','mock-provider','rule-engine-v1','1.0.0','p0-mock-v1','0ab5188dde564c939ba2be5e7db190bb','2026-03-29 22:12:18',1,1,'2026-03-29 22:12:18','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:12:18','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(5,5,'calm','confused','平静、家庭','{\"happy\":0.13,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.75,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.13,\"irritable\":0.13,\"sad\":0.13,\"warm\":0.13,\"confused\":0.48,\"hopeful\":0.13}','family',0,'情绪整体平稳','主情绪为calm，次情绪为confused。 关键词：平静、家庭。 整体情绪风险较低。','{\"mode\":\"mock-rule\",\"primaryEmotion\":\"calm\",\"secondaryEmotion\":\"confused\",\"riskLevel\":0,\"scene\":\"family\",\"keywords\":\"平静、家庭\",\"scores\":{\"happy\":0.13,\"anxious\":0.13,\"tired\":0.13,\"calm\":0.75,\"wronged\":0.13,\"expect\":0.13,\"lonely\":0.13,\"irritable\":0.13,\"sad\":0.13,\"warm\":0.13,\"confused\":0.48,\"hopeful\":0.13}}','mock-provider','rule-engine-v1','1.0.0','p0-mock-v1','e5148fdc151b4eaab52bc6c3ea9cc229','2026-03-29 22:12:40',1,1,'2026-03-29 22:12:40','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:12:40','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL);
/*!40000 ALTER TABLE `biz_ai_analysis_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_emotion_vector`
--

DROP TABLE IF EXISTS `biz_emotion_vector`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_emotion_vector` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `valence` decimal(6,4) DEFAULT NULL COMMENT '愉悦度（0-1）',
  `arousal` decimal(6,4) DEFAULT NULL COMMENT '激活度（0-1）',
  `anxiety` decimal(6,4) DEFAULT NULL COMMENT '焦虑度（0-1）',
  `calmness` decimal(6,4) DEFAULT NULL COMMENT '平静度（0-1）',
  `loneliness` decimal(6,4) DEFAULT NULL COMMENT '孤独度（0-1）',
  `fatigue` decimal(6,4) DEFAULT NULL COMMENT '疲惫度（0-1）',
  `anger` decimal(6,4) DEFAULT NULL COMMENT '烦躁度（0-1）',
  `hope` decimal(6,4) DEFAULT NULL COMMENT '希望感（0-1）',
  `confidence` decimal(6,4) DEFAULT NULL COMMENT '掌控感（0-1）',
  `dimension_json` text COLLATE utf8mb4_general_ci COMMENT '扩展维度JSON',
  `confidence_score` decimal(6,4) DEFAULT NULL COMMENT '置信度',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪向量表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_emotion_vector`
--

LOCK TABLES `biz_emotion_vector` WRITE;
/*!40000 ALTER TABLE `biz_emotion_vector` DISABLE KEYS */;
INSERT INTO `biz_emotion_vector` VALUES (1,1,0.8400,0.6200,0.1080,0.5180,0.1600,0.2360,0.1000,0.8600,0.7800,'{\"primaryEmotion\":\"happy\",\"secondaryEmotion\":\"calm\",\"intensityRate\":0.5,\"source\":\"rule-v1\"}',0.9200,'2026-03-17 22:03:11','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-17 22:03:11','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(2,2,0.2000,0.4400,0.5040,0.2480,0.7800,0.5240,0.2200,0.2200,0.3000,'{\"primaryEmotion\":\"sad\",\"secondaryEmotion\":\"lonely\",\"intensityRate\":0.5,\"source\":\"rule-v1\"}',0.7400,'2026-03-17 22:03:38','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-17 22:03:38','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(3,3,0.8400,0.6200,0.1080,0.5180,0.1600,0.2360,0.1000,0.8600,0.7800,'{\"primaryEmotion\":\"happy\",\"secondaryEmotion\":\"calm\",\"intensityRate\":0.5,\"source\":\"rule-v1\"}',0.9200,'2026-03-29 22:11:20','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:11:20','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(4,4,0.8400,0.6200,0.1080,0.5180,0.1600,0.2360,0.1000,0.8600,0.7800,'{\"primaryEmotion\":\"happy\",\"secondaryEmotion\":\"calm\",\"intensityRate\":0.5,\"source\":\"rule-v1\"}',0.9200,'2026-03-29 22:12:18','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:12:18','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(5,5,0.6400,0.3050,0.1260,0.8600,0.2400,0.3000,0.0800,0.6800,0.7200,'{\"primaryEmotion\":\"calm\",\"secondaryEmotion\":\"confused\",\"intensityRate\":0.5,\"source\":\"rule-v1\"}',0.9200,'2026-03-29 22:12:40','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:12:40','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL);
/*!40000 ALTER TABLE `biz_emotion_vector` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_globe_projection`
--

DROP TABLE IF EXISTS `biz_globe_projection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_globe_projection` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `projection_time` datetime NOT NULL COMMENT '投影时间',
  `province` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '城市',
  `district` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '区县',
  `location_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '位置名称',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `emotion_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '情绪类型',
  `intensity` tinyint DEFAULT NULL COMMENT '强度',
  `anonymous_id` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '匿名用户标识',
  `status` tinyint DEFAULT '1' COMMENT '状态（0隐藏 1显示）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_projection_time` (`projection_time`),
  KEY `idx_location_name` (`location_name`),
  KEY `idx_emotion_type` (`emotion_type`),
  KEY `idx_lon_lat` (`longitude`,`latitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='星球投影记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_globe_projection`
--

LOCK TABLES `biz_globe_projection` WRITE;
/*!40000 ALTER TABLE `biz_globe_projection` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_globe_projection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_mood_asset`
--

DROP TABLE IF EXISTS `biz_mood_asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_mood_asset` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `asset_type` tinyint NOT NULL COMMENT '素材类型（1图片 2语音）',
  `file_url` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件URL',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `mime_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'MIME类型',
  `duration` int DEFAULT NULL COMMENT '时长（秒）',
  `thumbnail_url` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '缩略图URL',
  `width` int DEFAULT NULL COMMENT '宽度',
  `height` int DEFAULT NULL COMMENT '高度',
  `status` tinyint DEFAULT '1' COMMENT '状态（0无效 1有效）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_record_id` (`record_id`),
  KEY `idx_asset_type` (`asset_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪素材表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_mood_asset`
--

LOCK TABLES `biz_mood_asset` WRITE;
/*!40000 ALTER TABLE `biz_mood_asset` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_mood_asset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_mood_record`
--

DROP TABLE IF EXISTS `biz_mood_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_mood_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `source_type` tinyint NOT NULL DEFAULT '1' COMMENT '来源端（1UniApp 2H5 3管理端 4API）',
  `record_type` tinyint NOT NULL DEFAULT '0' COMMENT '记录类型（0文本 1语音 2图片 3混合）',
  `content_text` text COLLATE utf8mb4_general_ci COMMENT '文本内容或语音转写文本',
  `voice_duration` int DEFAULT NULL COMMENT '语音时长（秒）',
  `emotion_intensity` tinyint DEFAULT '5' COMMENT '用户自评情绪强度（1-10）',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `record_status` tinyint NOT NULL DEFAULT '0' COMMENT '记录状态（0草稿 1已提交 2已归档）',
  `analyze_status` tinyint NOT NULL DEFAULT '0' COMMENT '分析状态（0待分析 1分析中 2成功 3失败）',
  `is_public` tinyint NOT NULL DEFAULT '0' COMMENT '是否公开到星球（0否 1是）',
  `province` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '城市',
  `district` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '区县',
  `location_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '位置名称',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `device_info` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设备信息',
  `risk_level` tinyint DEFAULT '0' COMMENT '风险等级（0无 1低 2中 3高）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_record_time` (`record_time`),
  KEY `idx_record_status` (`record_status`),
  KEY `idx_analyze_status` (`analyze_status`),
  KEY `idx_is_public` (`is_public`),
  KEY `idx_risk_level` (`risk_level`),
  KEY `idx_user_record_time` (`user_id`,`record_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪记录主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_mood_record`
--

LOCK TABLES `biz_mood_record` WRITE;
/*!40000 ALTER TABLE `biz_mood_record` DISABLE KEYS */;
INSERT INTO `biz_mood_record` VALUES (1,104,1,0,'今天很开心',NULL,5,'2026-03-17 22:03:11',1,1,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-03-17 22:03:11','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-17 22:03:11','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(2,104,1,0,'今天很伤心',NULL,5,'2026-03-17 22:03:38',1,1,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'2026-03-17 22:03:38','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-17 22:03:38','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(3,104,1,0,'开心，学习',NULL,5,'2026-03-29 22:11:20',1,1,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-03-29 22:11:20','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:11:20','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(4,104,1,0,'开心，学习',NULL,5,'2026-03-29 22:12:18',1,1,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-03-29 22:12:18','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:12:18','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(5,104,1,0,'平静，家庭',NULL,5,'2026-03-29 22:12:40',1,1,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-03-29 22:12:40','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:12:40','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL);
/*!40000 ALTER TABLE `biz_mood_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_mood_report`
--

DROP TABLE IF EXISTS `biz_mood_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_mood_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `report_type` tinyint NOT NULL COMMENT '报告类型（1日报 2周报 3月报）',
  `period_start` date NOT NULL COMMENT '开始日期',
  `period_end` date NOT NULL COMMENT '结束日期',
  `title` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报告标题',
  `summary` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'AI总结',
  `dominant_emotion` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主导情绪',
  `emotion_trend` text COLLATE utf8mb4_general_ci COMMENT '情绪趋势JSON',
  `statistic_json` text COLLATE utf8mb4_general_ci COMMENT '统计数据JSON',
  `pdf_url` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'PDF地址',
  `status` tinyint DEFAULT '1' COMMENT '状态（0生成中 1已完成 2失败）',
  `generate_time` datetime DEFAULT NULL COMMENT '生成完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_report_type` (`report_type`),
  KEY `idx_period_start` (`period_start`),
  KEY `idx_period_end` (`period_end`),
  KEY `idx_user_type_period` (`user_id`,`report_type`,`period_start`,`period_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪报告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_mood_report`
--

LOCK TABLES `biz_mood_report` WRITE;
/*!40000 ALTER TABLE `biz_mood_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_mood_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_notification`
--

DROP TABLE IF EXISTS `biz_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `type` tinyint NOT NULL COMMENT '通知类型（1系统通知 2报告生成 3风险提醒 4活动通知）',
  `title` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标题',
  `content` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `data` text COLLATE utf8mb4_general_ci COMMENT '附加数据JSON',
  `biz_id` bigint DEFAULT NULL COMMENT '业务ID',
  `jump_type` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '跳转类型',
  `jump_url` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '跳转地址',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读（0未读 1已读）',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `push_time` datetime DEFAULT NULL COMMENT '推送时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='消息通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_notification`
--

LOCK TABLES `biz_notification` WRITE;
/*!40000 ALTER TABLE `biz_notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_record_scene_rel`
--

DROP TABLE IF EXISTS `biz_record_scene_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_record_scene_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `scene_id` bigint NOT NULL COMMENT '场景ID',
  `source` tinyint DEFAULT '0' COMMENT '来源（0用户选择 1AI识别）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_scene` (`record_id`,`scene_id`),
  KEY `idx_scene_id` (`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪记录与场景关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_record_scene_rel`
--

LOCK TABLES `biz_record_scene_rel` WRITE;
/*!40000 ALTER TABLE `biz_record_scene_rel` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_record_scene_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_record_tag_rel`
--

DROP TABLE IF EXISTS `biz_record_tag_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_record_tag_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `source` tinyint DEFAULT '0' COMMENT '来源（0用户选择 1AI识别）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_tag` (`record_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪记录与标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_record_tag_rel`
--

LOCK TABLES `biz_record_tag_rel` WRITE;
/*!40000 ALTER TABLE `biz_record_tag_rel` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_record_tag_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_region_emotion_stats`
--

DROP TABLE IF EXISTS `biz_region_emotion_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_region_emotion_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `region_code` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '区域编码',
  `region_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '区域名称',
  `parent_code` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上级区域编码',
  `level` tinyint DEFAULT NULL COMMENT '级别（1国家 2省份 3城市）',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_hour` tinyint DEFAULT NULL COMMENT '统计小时（0-23）',
  `total_records` int DEFAULT '0' COMMENT '记录总数',
  `dominant_emotion` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '主导情绪',
  `emotion_distribution` text COLLATE utf8mb4_general_ci COMMENT '情绪分布JSON',
  `avg_intensity` decimal(6,4) DEFAULT NULL COMMENT '平均强度',
  `active_user_count` int DEFAULT '0' COMMENT '活跃用户数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_region_date_hour` (`region_code`,`stat_date`,`stat_hour`),
  KEY `idx_region_code` (`region_code`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='区域情绪统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_region_emotion_stats`
--

LOCK TABLES `biz_region_emotion_stats` WRITE;
/*!40000 ALTER TABLE `biz_region_emotion_stats` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_region_emotion_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_report_detail`
--

DROP TABLE IF EXISTS `biz_report_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_report_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_id` bigint NOT NULL COMMENT '报告ID',
  `section_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '区块类型',
  `content_json` longtext COLLATE utf8mb4_general_ci COMMENT '区块内容JSON',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  PRIMARY KEY (`id`),
  KEY `idx_report_id` (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报告详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_report_detail`
--

LOCK TABLES `biz_report_detail` WRITE;
/*!40000 ALTER TABLE `biz_report_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_report_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_risk_warning`
--

DROP TABLE IF EXISTS `biz_risk_warning`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_risk_warning` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `risk_level` tinyint NOT NULL COMMENT '风险等级（1低 2中 3高）',
  `risk_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '风险类型',
  `risk_reason` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '风险原因',
  `warning_content` text COLLATE utf8mb4_general_ci COMMENT '预警内容',
  `status` tinyint DEFAULT '0' COMMENT '处理状态（0未处理 1已处理 2已忽略）',
  `notify_status` tinyint DEFAULT '0' COMMENT '通知状态（0未通知 1已通知）',
  `notify_time` datetime DEFAULT NULL COMMENT '通知时间',
  `handler` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '处理人',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_result` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '处理结果',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_risk_level` (`risk_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='风险预警表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_risk_warning`
--

LOCK TABLES `biz_risk_warning` WRITE;
/*!40000 ALTER TABLE `biz_risk_warning` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_risk_warning` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_scene`
--

DROP TABLE IF EXISTS `biz_scene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_scene` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scene_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '场景名称',
  `scene_code` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '场景编码',
  `icon` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图标URL',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态（0禁用 1启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_scene_name` (`scene_name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='场景标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_scene`
--

LOCK TABLES `biz_scene` WRITE;
/*!40000 ALTER TABLE `biz_scene` DISABLE KEYS */;
INSERT INTO `biz_scene` VALUES (1,'工作','work','/icons/work.png',1,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(2,'学习','study','/icons/study.png',2,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(3,'家庭','family','/icons/family.png',3,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(4,'社交','social','/icons/social.png',4,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(5,'感情','love','/icons/love.png',5,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(6,'睡眠','sleep','/icons/sleep.png',6,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(7,'健康','health','/icons/health.png',7,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(8,'娱乐','entertainment','/icons/entertainment.png',8,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(9,'运动','sport','/icons/sport.png',9,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(10,'独处','alone','/icons/alone.png',10,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化');
/*!40000 ALTER TABLE `biz_scene` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_tag`
--

DROP TABLE IF EXISTS `biz_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tag_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签名称',
  `tag_code` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标签编码',
  `tag_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'emotion' COMMENT '标签类型（emotion情绪 custom自定义）',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态（0禁用 1启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_tag_name` (`tag_name`),
  KEY `idx_tag_type` (`tag_type`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_tag`
--

LOCK TABLES `biz_tag` WRITE;
/*!40000 ALTER TABLE `biz_tag` DISABLE KEYS */;
INSERT INTO `biz_tag` VALUES (1,'开心','happy','emotion',1,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(2,'焦虑','anxious','emotion',2,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(3,'疲惫','tired','emotion',3,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(4,'平静','calm','emotion',4,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(5,'委屈','wronged','emotion',5,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(6,'期待','expect','emotion',6,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(7,'孤独','lonely','emotion',7,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(8,'烦躁','irritable','emotion',8,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(9,'悲伤','sad','emotion',9,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(10,'温暖','warm','emotion',10,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(11,'迷茫','confused','emotion',11,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化'),(12,'充满希望','hopeful','emotion',12,1,'2026-03-16 22:15:20','system',NULL,NULL,0,'初始化');
/*!40000 ALTER TABLE `biz_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_user_auth`
--

DROP TABLE IF EXISTS `biz_user_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_user_auth` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '系统用户ID，关联sys_user.user_id',
  `auth_type` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '认证类型（wechat_mp小程序 wechat_app微信APP wechat_h5公众号）',
  `openid` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '微信openid',
  `unionid` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '微信unionid',
  `session_key` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '会话密钥',
  `auth_status` tinyint DEFAULT '1' COMMENT '状态（0禁用 1正常）',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_auth_type_openid` (`auth_type`,`openid`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_unionid` (`unionid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='第三方登录绑定表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_user_auth`
--

LOCK TABLES `biz_user_auth` WRITE;
/*!40000 ALTER TABLE `biz_user_auth` DISABLE KEYS */;
INSERT INTO `biz_user_auth` VALUES (5,104,'wechat_mp','oNM4B7IXiwxp24Kz4nE9fXVLqjao',NULL,'2tKT01y5MIC43hysrsygyw==',1,'2026-04-16 01:12:38','127.0.0.1','2026-03-17 16:07:33','wechat_mp','2026-04-16 01:12:38','wechat_mp',0,NULL);
/*!40000 ALTER TABLE `biz_user_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_user_config`
--

DROP TABLE IF EXISTS `biz_user_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_user_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `anonymous_projection` tinyint DEFAULT '1' COMMENT '是否允许匿名投影（0否 1是）',
  `ai_personalized_analysis` tinyint DEFAULT '1' COMMENT '是否开启个性化分析（0否 1是）',
  `save_origin_asset` tinyint DEFAULT '1' COMMENT '是否保留原始素材（0否 1是）',
  `daily_remind_enabled` tinyint DEFAULT '0' COMMENT '是否开启每日提醒（0否 1是）',
  `daily_remind_time` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '每日提醒时间，如21:00',
  `weekly_report_enabled` tinyint DEFAULT '1' COMMENT '是否开启周报提醒（0否 1是）',
  `language` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'zh-CN' COMMENT '语言',
  `theme_mode` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'auto' COMMENT '主题模式',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_user_config`
--

LOCK TABLES `biz_user_config` WRITE;
/*!40000 ALTER TABLE `biz_user_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_user_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_user_info`
--

DROP TABLE IF EXISTS `biz_user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_user_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID，关联sys_user.user_id',
  `nick_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT '0' COMMENT '性别（0未知 1男 2女）',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `signature` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '个性签名',
  `emotion_level` int DEFAULT '1' COMMENT '情绪等级',
  `record_days` int DEFAULT '0' COMMENT '连续记录天数',
  `total_records` int DEFAULT '0' COMMENT '累计记录数',
  `anonymous_projection` tinyint DEFAULT '1' COMMENT '是否允许匿名投影（0关闭 1开启）',
  `last_record_time` datetime DEFAULT NULL COMMENT '最后一次记录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_nick_name` (`nick_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户扩展信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_user_info`
--

LOCK TABLES `biz_user_info` WRITE;
/*!40000 ALTER TABLE `biz_user_info` DISABLE KEYS */;
INSERT INTO `biz_user_info` VALUES (5,104,'mood_user',NULL,0,NULL,NULL,1,0,0,1,NULL,'2026-03-17 16:07:34','wechat_mp',NULL,NULL,0,NULL);
/*!40000 ALTER TABLE `biz_user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_weather_mapping`
--

DROP TABLE IF EXISTS `biz_weather_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_weather_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `weather_code` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '天气编码',
  `weather_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '天气名称',
  `sky_type` tinyint DEFAULT NULL COMMENT '天空类型',
  `cloud_density` tinyint DEFAULT NULL COMMENT '云层密度（0-10）',
  `rain_intensity` tinyint DEFAULT NULL COMMENT '降雨强度（0-10）',
  `lightning_intensity` tinyint DEFAULT NULL COMMENT '雷电强度（0-10）',
  `wind_speed` tinyint DEFAULT NULL COMMENT '风速等级（0-10）',
  `fog_intensity` tinyint DEFAULT NULL COMMENT '雾气等级（0-10）',
  `color_temperature` int DEFAULT NULL COMMENT '色温（K）',
  `saturation` tinyint DEFAULT NULL COMMENT '饱和度（0-100）',
  `particle_style` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '粒子风格',
  `animation_seed` int DEFAULT NULL COMMENT '动画种子',
  `extra_params` text COLLATE utf8mb4_general_ci COMMENT '扩展参数JSON',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_weather_code` (`weather_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪天气映射结果表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_weather_mapping`
--

LOCK TABLES `biz_weather_mapping` WRITE;
/*!40000 ALTER TABLE `biz_weather_mapping` DISABLE KEYS */;
INSERT INTO `biz_weather_mapping` VALUES (1,1,'sunny','sunny',1,2,0,0,2,1,6700,86,'sunshine',1,'{\"valence\":0.8400,\"arousal\":0.6200,\"anxiety\":0.1080,\"calmness\":0.5180,\"hope\":0.8600,\"confidence\":0.7800}','2026-03-17 22:03:11','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-17 22:03:11','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(2,2,'rain','rain',3,7,6,1,4,3,5000,52,'rain',2,'{\"valence\":0.2000,\"arousal\":0.4400,\"anxiety\":0.5040,\"calmness\":0.2480,\"hope\":0.2200,\"confidence\":0.3000}','2026-03-17 22:03:38','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-17 22:03:38','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(3,3,'sunny','sunny',1,2,0,0,2,1,6700,86,'sunshine',3,'{\"valence\":0.8400,\"arousal\":0.6200,\"anxiety\":0.1080,\"calmness\":0.5180,\"hope\":0.8600,\"confidence\":0.7800}','2026-03-29 22:11:20','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:11:20','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(4,4,'sunny','sunny',1,2,0,0,2,1,6700,86,'sunshine',4,'{\"valence\":0.8400,\"arousal\":0.6200,\"anxiety\":0.1080,\"calmness\":0.5180,\"hope\":0.8600,\"confidence\":0.7800}','2026-03-29 22:12:19','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:12:19','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(5,5,'breeze','breeze',2,3,0,0,4,1,6100,72,'breeze',5,'{\"valence\":0.6400,\"arousal\":0.3050,\"anxiety\":0.1260,\"calmness\":0.8600,\"hope\":0.6800,\"confidence\":0.7200}','2026-03-29 22:12:40','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:12:40','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL);
/*!40000 ALTER TABLE `biz_weather_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_weather_snapshot`
--

DROP TABLE IF EXISTS `biz_weather_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_weather_snapshot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `snapshot_date` date NOT NULL COMMENT '快照日期',
  `weather_code` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '天气编码',
  `weather_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '天气名称',
  `sky_type` tinyint DEFAULT NULL COMMENT '天空类型',
  `cloud_density` tinyint DEFAULT NULL COMMENT '云层密度',
  `rain_intensity` tinyint DEFAULT NULL COMMENT '降雨强度',
  `lightning_intensity` tinyint DEFAULT NULL COMMENT '雷电强度',
  `wind_speed` tinyint DEFAULT NULL COMMENT '风速等级',
  `fog_intensity` tinyint DEFAULT NULL COMMENT '雾气等级',
  `color_temperature` int DEFAULT NULL COMMENT '色温',
  `saturation` tinyint DEFAULT NULL COMMENT '饱和度',
  `particle_style` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '粒子风格',
  `animation_seed` int DEFAULT NULL COMMENT '动画种子',
  `combined_vector` text COLLATE utf8mb4_general_ci COMMENT '综合向量JSON',
  `record_count` int DEFAULT '0' COMMENT '当日记录数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_snapshot_date` (`user_id`,`snapshot_date`),
  KEY `idx_snapshot_date` (`snapshot_date`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='天气快照表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_weather_snapshot`
--

LOCK TABLES `biz_weather_snapshot` WRITE;
/*!40000 ALTER TABLE `biz_weather_snapshot` DISABLE KEYS */;
INSERT INTO `biz_weather_snapshot` VALUES (1,104,'2026-03-17','cloudy','cloudy',2,6,1,0,3,2,5600,64,'cloud',104,'{\"valence\":0.52000000,\"arousal\":0.53000000,\"anxiety\":0.30600000,\"calmness\":0.38300000,\"loneliness\":0.47000000,\"fatigue\":0.38000000,\"anger\":0.16000000,\"hope\":0.54000000,\"confidence\":0.54000000}',2,'2026-03-17 22:03:11','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-17 22:03:38','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL),(3,104,'2026-03-29','sunny','sunny',1,2,0,0,2,1,6700,86,'sunshine',104,'{\"valence\":0.77333333,\"arousal\":0.51500000,\"anxiety\":0.11400000,\"calmness\":0.63200000,\"loneliness\":0.18666667,\"fatigue\":0.25733333,\"anger\":0.09333333,\"hope\":0.80000000,\"confidence\":0.76000000}',3,'2026-03-29 22:11:20','wx_b7ixiwxp24kz4ne9fxvlqjao','2026-03-29 22:12:40','wx_b7ixiwxp24kz4ne9fxvlqjao',0,NULL);
/*!40000 ALTER TABLE `biz_weather_snapshot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--

LOCK TABLES `qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2026-03-16 20:57:16','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2026-03-16 20:57:16','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2026-03-16 20:57:16','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','true','Y','admin','2026-03-16 20:57:16','',NULL,'是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2026-03-16 20:57:16','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2026-03-16 20:57:16','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2026-03-16 20:57:16','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2026-03-16 20:57:16','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','若依科技',0,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL),(101,100,'0,100','深圳总公司',1,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL),(102,100,'0,100','长沙分公司',2,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL),(103,101,'0,100,101','研发部门',1,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL),(104,101,'0,100,101','市场部门',2,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL),(105,101,'0,100,101','测试部门',3,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL),(106,101,'0,100,101','财务部门',4,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL),(107,101,'0,100,101','运维部门',5,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL),(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL),(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-16 20:57:16','',NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2026-03-16 20:57:16','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2026-03-16 20:57:16','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2026-03-16 20:57:16','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2026-03-16 20:57:16','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2026-03-16 20:57:16','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2026-03-16 20:57:16','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2026-03-16 20:57:16','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2026-03-16 20:57:16','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2026-03-16 20:57:16','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2026-03-16 20:57:16','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2026-03-16 20:57:16','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2026-03-16 20:57:16','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2026-03-16 20:57:16','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2026-03-16 20:57:16','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2026-03-16 20:57:16','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2026-03-16 20:57:16','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2026-03-16 20:57:16','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2026-03-16 20:57:16','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2026-03-16 20:57:16','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2026-03-16 20:57:16','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2026-03-16 20:57:16','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2026-03-16 20:57:16','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2026-03-16 20:57:16','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2026-03-16 20:57:16','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2026-03-16 20:57:16','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2026-03-16 20:57:16','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2026-03-16 20:57:16','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2026-03-16 20:57:16','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2026-03-16 20:57:16','',NULL,'停用状态');
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2026-03-16 20:57:16','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2026-03-16 20:57:16','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2026-03-16 20:57:16','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2026-03-16 20:57:16','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2026-03-16 20:57:16','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2026-03-16 20:57:16','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2026-03-16 20:57:16','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2026-03-16 20:57:16','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2026-03-16 20:57:16','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2026-03-16 20:57:16','',NULL,'登录状态列表');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2026-03-16 20:57:17','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2026-03-16 20:57:17','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2026-03-16 20:57:17','',NULL,'');
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
INSERT INTO `sys_logininfor` VALUES (100,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-16 22:52:49'),(101,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-17 16:09:51'),(102,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-17 16:49:31'),(103,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-17 16:49:34'),(104,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-17 20:17:25'),(105,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-17 21:55:16'),(106,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-17 21:55:26'),(107,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-29 21:11:27');
/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,1,'system',NULL,'','',1,0,'M','0','0','','system','admin','2026-03-16 20:57:16','',NULL,'系统管理目录'),(2,'系统监控',0,2,'monitor',NULL,'','',1,0,'M','0','0','','monitor','admin','2026-03-16 20:57:16','',NULL,'系统监控目录'),(3,'系统工具',0,3,'tool',NULL,'','',1,0,'M','0','0','','tool','admin','2026-03-16 20:57:16','',NULL,'系统工具目录'),(4,'若依官网',0,4,'http://ruoyi.vip',NULL,'','',0,0,'M','0','0','','guide','admin','2026-03-16 20:57:16','',NULL,'若依官网地址'),(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2026-03-16 20:57:16','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2026-03-16 20:57:16','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','admin','2026-03-16 20:57:16','',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2026-03-16 20:57:16','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','0','0','system:post:list','post','admin','2026-03-16 20:57:16','',NULL,'岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','admin','2026-03-16 20:57:16','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','0','0','system:config:list','edit','admin','2026-03-16 20:57:16','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','0','0','system:notice:list','message','admin','2026-03-16 20:57:16','',NULL,'通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2026-03-16 20:57:16','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2026-03-16 20:57:16','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','admin','2026-03-16 20:57:16','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','monitor/druid/index','','',1,0,'C','0','0','monitor:druid:list','druid','admin','2026-03-16 20:57:16','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','monitor/server/index','','',1,0,'C','0','0','monitor:server:list','server','admin','2026-03-16 20:57:16','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','','',1,0,'C','0','0','monitor:cache:list','redis','admin','2026-03-16 20:57:16','',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','monitor/cache/list','','',1,0,'C','0','0','monitor:cache:list','redis-list','admin','2026-03-16 20:57:16','',NULL,'缓存列表菜单'),(115,'表单构建',3,1,'build','tool/build/index','','',1,0,'C','0','0','tool:build:list','build','admin','2026-03-16 20:57:16','',NULL,'表单构建菜单'),(116,'代码生成',3,2,'gen','tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','admin','2026-03-16 20:57:16','',NULL,'代码生成菜单'),(117,'系统接口',3,3,'swagger','tool/swagger/index','','',1,0,'C','0','0','tool:swagger:list','swagger','admin','2026-03-16 20:57:16','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2026-03-16 20:57:16','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2026-03-16 20:57:16','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2026-03-16 20:57:16','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2026-03-16 20:57:16','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2026-03-16 20:57:16','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2026-03-16 20:57:16','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2026-03-16 20:57:16','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2026-03-16 20:57:16','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2026-03-16 20:57:16','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2026-03-16 20:57:16','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2026-03-16 20:57:16','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2026-03-16 20:57:16','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2026-03-16 20:57:16','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2026-03-16 20:57:16','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2026-03-16 20:57:16','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2026-03-16 20:57:16','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2026-03-16 20:57:16','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2026-03-16 20:57:16','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2026-03-16 20:57:16','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2026-03-16 20:57:16','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2026-03-16 20:57:16','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2026-03-16 20:57:16','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','admin','2026-03-16 20:57:16','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','admin','2026-03-16 20:57:16','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','admin','2026-03-16 20:57:16','',NULL,''),(1055,'生成查询',116,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','admin','2026-03-16 20:57:16','',NULL,''),(1056,'生成修改',116,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','admin','2026-03-16 20:57:16','',NULL,''),(1057,'生成删除',116,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','admin','2026-03-16 20:57:16','',NULL,''),(1058,'导入代码',116,4,'#','','','',1,0,'F','0','0','tool:gen:import','#','admin','2026-03-16 20:57:16','',NULL,''),(1059,'预览代码',116,5,'#','','','',1,0,'F','0','0','tool:gen:preview','#','admin','2026-03-16 20:57:16','',NULL,''),(1060,'生成代码',116,6,'#','','','',1,0,'F','0','0','tool:gen:code','#','admin','2026-03-16 20:57:16','',NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 若依新版本发布啦','2',_binary '新版本内容','0','admin','2026-03-16 20:57:17','',NULL,'管理员'),(2,'维护通知：2018-07-01 若依系统凌晨维护','1',_binary '维护内容','0','admin','2026-03-16 20:57:17','',NULL,'管理员');
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (100,'角色管理',1,'com.moodsphere.web.controller.system.SysRoleController.add()','POST',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createBy\":\"admin\",\"deptCheckStrictly\":true,\"deptIds\":[],\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"移动端普通用户\",\"roleId\":100,\"roleKey\":\"app_user\",\"roleName\":\"普通用户\",\"roleSort\":2,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-17 16:22:53',35),(101,'角色管理',2,'com.moodsphere.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-17 16:22:52\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"移动端普通用户\",\"roleId\":100,\"roleKey\":\"app_user\",\"roleName\":\"普通用户\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-17 16:23:02',29),(102,'角色管理',1,'com.moodsphere.web.controller.system.SysRoleController.add()','POST',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createBy\":\"admin\",\"deptCheckStrictly\":true,\"deptIds\":[],\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"负责标签、文案、勋章、通知等业务配置\",\"roleId\":101,\"roleKey\":\"ops_manager\",\"roleName\":\"运营主管\",\"roleSort\":4,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-17 16:24:01',27),(103,'角色管理',1,'com.moodsphere.web.controller.system.SysRoleController.add()','POST',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createBy\":\"admin\",\"deptCheckStrictly\":true,\"deptIds\":[],\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"负责 Prompt 模板、天气映射规则、模型参数调优\",\"roleId\":102,\"roleKey\":\"ai_strategist\",\"roleName\":\"算法策略师\",\"roleSort\":5,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-17 16:24:32',27),(104,'角色管理',1,'com.moodsphere.web.controller.system.SysRoleController.add()','POST',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createBy\":\"admin\",\"deptCheckStrictly\":true,\"deptIds\":[],\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"负责敏感词过滤、高危风险处理、用户隐私申诉\",\"roleId\":103,\"roleKey\":\"safety_officer\",\"roleName\":\"安全合规员\",\"roleSort\":6,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-17 16:25:01',21),(105,'角色管理',1,'com.moodsphere.web.controller.system.SysRoleController.add()','POST',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createBy\":\"admin\",\"deptCheckStrictly\":true,\"deptIds\":[],\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"仅拥有查看各类统计看板和导出脱敏报表的权限\",\"roleId\":104,\"roleKey\":\"data_analyst\",\"roleName\":\"数据分析员\",\"roleSort\":7,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-17 16:25:29',19),(106,'角色管理',2,'com.moodsphere.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-17 16:22:52\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"仅用于移动端 API 权限校验，严禁分配菜单权限\",\"roleId\":100,\"roleKey\":\"app_user\",\"roleName\":\"普通用户\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-17 16:27:36',19);
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2026-03-16 20:57:16','',NULL,''),(2,'se','项目经理',2,'0','admin','2026-03-16 20:57:16','',NULL,''),(3,'hr','人力资源',3,'0','admin','2026-03-16 20:57:16','',NULL,''),(4,'user','普通员工',4,'0','admin','2026-03-16 20:57:16','',NULL,'');
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2026-03-16 20:57:16','',NULL,'超级管理员'),(2,'普通角色','common',2,'2',1,1,'0','0','admin','2026-03-16 20:57:16','',NULL,'普通角色'),(100,'普通用户','app_user',3,'1',1,1,'0','0','admin','2026-03-17 16:22:52','admin','2026-03-17 16:27:36','仅用于移动端 API 权限校验，严禁分配菜单权限'),(101,'运营主管','ops_manager',4,'1',1,1,'0','0','admin','2026-03-17 16:24:01','',NULL,'负责标签、文案、勋章、通知等业务配置'),(102,'算法策略师','ai_strategist',5,'1',1,1,'0','0','admin','2026-03-17 16:24:32','',NULL,'负责 Prompt 模板、天气映射规则、模型参数调优'),(103,'安全合规员','safety_officer',6,'1',1,1,'0','0','admin','2026-03-17 16:25:01','',NULL,'负责敏感词过滤、高危风险处理、用户隐私申诉'),(104,'数据分析员','data_analyst',7,'1',1,1,'0','0','admin','2026-03-17 16:25:29','',NULL,'仅拥有查看各类统计看板和导出脱敏报表的权限');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (2,1),(2,2),(2,3),(2,4),(2,100),(2,101),(2,102),(2,103),(2,104),(2,105),(2,106),(2,107),(2,108),(2,109),(2,110),(2,111),(2,112),(2,113),(2,114),(2,115),(2,116),(2,117),(2,500),(2,501),(2,1000),(2,1001),(2,1002),(2,1003),(2,1004),(2,1005),(2,1006),(2,1007),(2,1008),(2,1009),(2,1010),(2,1011),(2,1012),(2,1013),(2,1014),(2,1015),(2,1016),(2,1017),(2,1018),(2,1019),(2,1020),(2,1021),(2,1022),(2,1023),(2,1024),(2,1025),(2,1026),(2,1027),(2,1028),(2,1029),(2,1030),(2,1031),(2,1032),(2,1033),(2,1034),(2,1035),(2,1036),(2,1037),(2,1038),(2,1039),(2,1040),(2,1041),(2,1042),(2,1043),(2,1044),(2,1045),(2,1046),(2,1047),(2,1048),(2,1049),(2,1050),(2,1051),(2,1052),(2,1053),(2,1054),(2,1055),(2,1056),(2,1057),(2,1058),(2,1059),(2,1060);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,103,'admin','若依','00','ry@163.com','15888888888','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-03-29 21:11:22','2026-03-16 20:57:16','admin','2026-03-16 20:57:16','',NULL,'管理员'),(2,105,'ry','若依','00','ry@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-03-16 20:57:16','2026-03-16 20:57:16','admin','2026-03-16 20:57:16','',NULL,'测试员'),(104,100,'wx_b7ixiwxp24kz4ne9fxvlqjao','mood_user','00','','','2','','$2a$10$/q0CNdJixawiWEiK36ugIevKrtBCgVuxty6YprYJ/DQSrYh.Srfte','0','0','',NULL,NULL,'wechat_mp','2026-03-17 16:07:33','',NULL,NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,2),(104,2);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-22 15:31:13
