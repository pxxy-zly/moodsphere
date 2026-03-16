-- 情绪天气项目数据库建表脚本
-- 数据库名：moodsphere
-- 字符集：utf8mb4
-- 适用于 MySQL 8.x

-- 创建数据库（如果不存在）
# CREATE DATABASE IF NOT EXISTS `moodsphere`
# DEFAULT CHARACTER SET utf8mb4
# COLLATE utf8mb4_general_ci;
#
# USE `moodsphere`;

-- ========================================================
-- 一、基础配置模块
-- ========================================================

-- 1. 用户扩展信息表
DROP TABLE IF EXISTS `biz_user_info`;
CREATE TABLE `biz_user_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID，关联sys_user.user_id',
  `nick_name` varchar(50) NOT NULL COMMENT '昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT '0' COMMENT '性别（0未知 1男 2女）',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `signature` varchar(200) DEFAULT NULL COMMENT '个性签名',
  `emotion_level` int DEFAULT '1' COMMENT '情绪等级',
  `record_days` int DEFAULT '0' COMMENT '连续记录天数',
  `total_records` int DEFAULT '0' COMMENT '累计记录数',
  `anonymous_projection` tinyint DEFAULT '1' COMMENT '是否允许匿名投影（0关闭 1开启）',
  `last_record_time` datetime DEFAULT NULL COMMENT '最后一次记录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_nick_name` (`nick_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户扩展信息表';

-- 2. 情绪标签表
DROP TABLE IF EXISTS `biz_tag`;
CREATE TABLE `biz_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `tag_code` varchar(50) DEFAULT NULL COMMENT '标签编码',
  `tag_type` varchar(20) NOT NULL DEFAULT 'emotion' COMMENT '标签类型（emotion情绪 custom自定义）',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态（0禁用 1启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_tag_name` (`tag_name`),
  KEY `idx_tag_type` (`tag_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪标签表';

-- 3. 场景标签表
DROP TABLE IF EXISTS `biz_scene`;
CREATE TABLE `biz_scene` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scene_name` varchar(50) NOT NULL COMMENT '场景名称',
  `scene_code` varchar(50) DEFAULT NULL COMMENT '场景编码',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标URL',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态（0禁用 1启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_scene_name` (`scene_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='场景标签表';

-- ========================================================
-- 二、情绪记录模块
-- ========================================================

-- 4. 情绪记录主表
DROP TABLE IF EXISTS `biz_mood_record`;
CREATE TABLE `biz_mood_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `source_type` tinyint NOT NULL DEFAULT '1' COMMENT '来源端（1UniApp 2H5 3管理端 4API）',
  `record_type` tinyint NOT NULL DEFAULT '0' COMMENT '记录类型（0文本 1语音 2图片 3混合）',
  `content_text` text COMMENT '文本内容或语音转写文本',
  `voice_duration` int DEFAULT NULL COMMENT '语音时长（秒）',
  `emotion_intensity` tinyint DEFAULT '5' COMMENT '用户自评情绪强度（1-10）',
  `record_time` datetime NOT NULL COMMENT '记录时间',
  `record_status` tinyint NOT NULL DEFAULT '0' COMMENT '记录状态（0草稿 1已提交 2已归档）',
  `analyze_status` tinyint NOT NULL DEFAULT '0' COMMENT '分析状态（0待分析 1分析中 2成功 3失败）',
  `is_public` tinyint NOT NULL DEFAULT '0' COMMENT '是否公开到星球（0否 1是）',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `location_name` varchar(100) DEFAULT NULL COMMENT '位置名称',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `device_info` varchar(255) DEFAULT NULL COMMENT '设备信息',
  `risk_level` tinyint DEFAULT '0' COMMENT '风险等级（0无 1低 2中 3高）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_record_time` (`record_time`),
  KEY `idx_record_status` (`record_status`),
  KEY `idx_analyze_status` (`analyze_status`),
  KEY `idx_is_public` (`is_public`),
  KEY `idx_risk_level` (`risk_level`),
  KEY `idx_user_record_time` (`user_id`,`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪记录主表';

-- 5. 情绪记录与标签关联表
DROP TABLE IF EXISTS `biz_record_tag_rel`;
CREATE TABLE `biz_record_tag_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `source` tinyint DEFAULT '0' COMMENT '来源（0用户选择 1AI识别）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_tag` (`record_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪记录与标签关联表';

-- 6. 情绪记录与场景关联表
DROP TABLE IF EXISTS `biz_record_scene_rel`;
CREATE TABLE `biz_record_scene_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `scene_id` bigint NOT NULL COMMENT '场景ID',
  `source` tinyint DEFAULT '0' COMMENT '来源（0用户选择 1AI识别）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_scene` (`record_id`,`scene_id`),
  KEY `idx_scene_id` (`scene_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪记录与场景关联表';

-- 7. 情绪素材表
DROP TABLE IF EXISTS `biz_mood_asset`;
CREATE TABLE `biz_mood_asset` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `asset_type` tinyint NOT NULL COMMENT '素材类型（1图片 2语音）',
  `file_url` varchar(500) NOT NULL COMMENT '文件URL',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `mime_type` varchar(100) DEFAULT NULL COMMENT 'MIME类型',
  `duration` int DEFAULT NULL COMMENT '时长（秒）',
  `thumbnail_url` varchar(500) DEFAULT NULL COMMENT '缩略图URL',
  `width` int DEFAULT NULL COMMENT '宽度',
  `height` int DEFAULT NULL COMMENT '高度',
  `status` tinyint DEFAULT '1' COMMENT '状态（0无效 1有效）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_record_id` (`record_id`),
  KEY `idx_asset_type` (`asset_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪素材表';

-- ========================================================
-- 三、AI分析模块
-- ========================================================

-- 8. AI分析结果表
DROP TABLE IF EXISTS `biz_ai_analysis_result`;
CREATE TABLE `biz_ai_analysis_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `primary_emotion` varchar(50) DEFAULT NULL COMMENT '主情绪',
  `secondary_emotion` varchar(50) DEFAULT NULL COMMENT '次情绪',
  `emotion_keywords` varchar(500) DEFAULT NULL COMMENT '情绪关键词，逗号分隔',
  `emotion_scores` text COMMENT '情绪维度得分JSON',
  `scene_recognition` varchar(255) DEFAULT NULL COMMENT '识别场景',
  `risk_level` tinyint DEFAULT '0' COMMENT '风险等级（0无 1低 2中 3高）',
  `risk_reason` varchar(255) DEFAULT NULL COMMENT '风险原因',
  `ai_summary` varchar(500) DEFAULT NULL COMMENT 'AI摘要',
  `raw_response` longtext COMMENT 'AI原始返回',
  `provider` varchar(50) DEFAULT NULL COMMENT '模型供应商',
  `model_name` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `model_version` varchar(50) DEFAULT NULL COMMENT '模型版本',
  `prompt_version` varchar(50) DEFAULT NULL COMMENT 'Prompt版本',
  `request_id` varchar(100) DEFAULT NULL COMMENT '请求链路ID',
  `analysis_at` datetime DEFAULT NULL COMMENT '分析完成时间',
  `analysis_cost_ms` int DEFAULT NULL COMMENT '分析耗时毫秒',
  `status` tinyint DEFAULT '1' COMMENT '状态（0失败 1成功）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_primary_emotion` (`primary_emotion`),
  KEY `idx_risk_level` (`risk_level`),
  KEY `idx_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI分析结果表';

-- 9. 情绪向量表
DROP TABLE IF EXISTS `biz_emotion_vector`;
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
  `dimension_json` text COMMENT '扩展维度JSON',
  `confidence_score` decimal(6,4) DEFAULT NULL COMMENT '置信度',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪向量表';

-- ========================================================
-- 四、天气映射模块
-- ========================================================

-- 10. 情绪天气映射结果表
DROP TABLE IF EXISTS `biz_weather_mapping`;
CREATE TABLE `biz_weather_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `weather_code` varchar(20) DEFAULT NULL COMMENT '天气编码',
  `weather_name` varchar(50) DEFAULT NULL COMMENT '天气名称',
  `sky_type` tinyint DEFAULT NULL COMMENT '天空类型',
  `cloud_density` tinyint DEFAULT NULL COMMENT '云层密度（0-10）',
  `rain_intensity` tinyint DEFAULT NULL COMMENT '降雨强度（0-10）',
  `lightning_intensity` tinyint DEFAULT NULL COMMENT '雷电强度（0-10）',
  `wind_speed` tinyint DEFAULT NULL COMMENT '风速等级（0-10）',
  `fog_intensity` tinyint DEFAULT NULL COMMENT '雾气等级（0-10）',
  `color_temperature` int DEFAULT NULL COMMENT '色温（K）',
  `saturation` tinyint DEFAULT NULL COMMENT '饱和度（0-100）',
  `particle_style` varchar(20) DEFAULT NULL COMMENT '粒子风格',
  `animation_seed` int DEFAULT NULL COMMENT '动画种子',
  `extra_params` text COMMENT '扩展参数JSON',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_weather_code` (`weather_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪天气映射结果表';

-- 11. 天气快照表
DROP TABLE IF EXISTS `biz_weather_snapshot`;
CREATE TABLE `biz_weather_snapshot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `snapshot_date` date NOT NULL COMMENT '快照日期',
  `weather_code` varchar(20) DEFAULT NULL COMMENT '天气编码',
  `weather_name` varchar(50) DEFAULT NULL COMMENT '天气名称',
  `sky_type` tinyint DEFAULT NULL COMMENT '天空类型',
  `cloud_density` tinyint DEFAULT NULL COMMENT '云层密度',
  `rain_intensity` tinyint DEFAULT NULL COMMENT '降雨强度',
  `lightning_intensity` tinyint DEFAULT NULL COMMENT '雷电强度',
  `wind_speed` tinyint DEFAULT NULL COMMENT '风速等级',
  `fog_intensity` tinyint DEFAULT NULL COMMENT '雾气等级',
  `color_temperature` int DEFAULT NULL COMMENT '色温',
  `saturation` tinyint DEFAULT NULL COMMENT '饱和度',
  `particle_style` varchar(20) DEFAULT NULL COMMENT '粒子风格',
  `animation_seed` int DEFAULT NULL COMMENT '动画种子',
  `combined_vector` text COMMENT '综合向量JSON',
  `record_count` int DEFAULT '0' COMMENT '当日记录数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_snapshot_date` (`user_id`,`snapshot_date`),
  KEY `idx_snapshot_date` (`snapshot_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='天气快照表';

-- ========================================================
-- 五、报告模块
-- ========================================================

-- 12. 情绪报告表
DROP TABLE IF EXISTS `biz_mood_report`;
CREATE TABLE `biz_mood_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `report_type` tinyint NOT NULL COMMENT '报告类型（1日报 2周报 3月报）',
  `period_start` date NOT NULL COMMENT '开始日期',
  `period_end` date NOT NULL COMMENT '结束日期',
  `title` varchar(100) DEFAULT NULL COMMENT '报告标题',
  `summary` varchar(500) DEFAULT NULL COMMENT 'AI总结',
  `dominant_emotion` varchar(50) DEFAULT NULL COMMENT '主导情绪',
  `emotion_trend` text COMMENT '情绪趋势JSON',
  `statistic_json` text COMMENT '统计数据JSON',
  `pdf_url` varchar(500) DEFAULT NULL COMMENT 'PDF地址',
  `status` tinyint DEFAULT '1' COMMENT '状态（0生成中 1已完成 2失败）',
  `generate_time` datetime DEFAULT NULL COMMENT '生成完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_report_type` (`report_type`),
  KEY `idx_period_start` (`period_start`),
  KEY `idx_period_end` (`period_end`),
  KEY `idx_user_type_period` (`user_id`,`report_type`,`period_start`,`period_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='情绪报告表';

-- 13. 报告详情表
DROP TABLE IF EXISTS `biz_report_detail`;
CREATE TABLE `biz_report_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_id` bigint NOT NULL COMMENT '报告ID',
  `section_type` varchar(50) NOT NULL COMMENT '区块类型',
  `content_json` longtext COMMENT '区块内容JSON',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  PRIMARY KEY (`id`),
  KEY `idx_report_id` (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报告详情表';

-- ========================================================
-- 六、星球投影模块
-- ========================================================

-- 14. 星球投影记录表
DROP TABLE IF EXISTS `biz_globe_projection`;
CREATE TABLE `biz_globe_projection` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `projection_time` datetime NOT NULL COMMENT '投影时间',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `location_name` varchar(100) DEFAULT NULL COMMENT '位置名称',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `emotion_type` varchar(20) DEFAULT NULL COMMENT '情绪类型',
  `intensity` tinyint DEFAULT NULL COMMENT '强度',
  `anonymous_id` varchar(64) DEFAULT NULL COMMENT '匿名用户标识',
  `status` tinyint DEFAULT '1' COMMENT '状态（0隐藏 1显示）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_projection_time` (`projection_time`),
  KEY `idx_location_name` (`location_name`),
  KEY `idx_emotion_type` (`emotion_type`),
  KEY `idx_lon_lat` (`longitude`,`latitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='星球投影记录表';

-- 15. 区域情绪统计表
DROP TABLE IF EXISTS `biz_region_emotion_stats`;
CREATE TABLE `biz_region_emotion_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `region_code` varchar(20) NOT NULL COMMENT '区域编码',
  `region_name` varchar(50) NOT NULL COMMENT '区域名称',
  `parent_code` varchar(20) DEFAULT NULL COMMENT '上级区域编码',
  `level` tinyint DEFAULT NULL COMMENT '级别（1国家 2省份 3城市）',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_hour` tinyint DEFAULT NULL COMMENT '统计小时（0-23）',
  `total_records` int DEFAULT '0' COMMENT '记录总数',
  `dominant_emotion` varchar(20) DEFAULT NULL COMMENT '主导情绪',
  `emotion_distribution` text COMMENT '情绪分布JSON',
  `avg_intensity` decimal(6,4) DEFAULT NULL COMMENT '平均强度',
  `active_user_count` int DEFAULT '0' COMMENT '活跃用户数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_region_date_hour` (`region_code`,`stat_date`,`stat_hour`),
  KEY `idx_region_code` (`region_code`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='区域情绪统计表';

-- ========================================================
-- 七、风险预警模块
-- ========================================================

-- 16. 风险预警表
DROP TABLE IF EXISTS `biz_risk_warning`;
CREATE TABLE `biz_risk_warning` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `risk_level` tinyint NOT NULL COMMENT '风险等级（1低 2中 3高）',
  `risk_type` varchar(50) DEFAULT NULL COMMENT '风险类型',
  `risk_reason` varchar(255) DEFAULT NULL COMMENT '风险原因',
  `warning_content` text COMMENT '预警内容',
  `status` tinyint DEFAULT '0' COMMENT '处理状态（0未处理 1已处理 2已忽略）',
  `notify_status` tinyint DEFAULT '0' COMMENT '通知状态（0未通知 1已通知）',
  `notify_time` datetime DEFAULT NULL COMMENT '通知时间',
  `handler` varchar(64) DEFAULT NULL COMMENT '处理人',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_result` varchar(255) DEFAULT NULL COMMENT '处理结果',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_id` (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_risk_level` (`risk_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='风险预警表';

-- ========================================================
-- 八、通知模块
-- ========================================================

-- 17. 消息通知表
DROP TABLE IF EXISTS `biz_notification`;
CREATE TABLE `biz_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `type` tinyint NOT NULL COMMENT '通知类型（1系统通知 2报告生成 3风险提醒 4活动通知）',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `content` varchar(500) NOT NULL COMMENT '内容',
  `data` text COMMENT '附加数据JSON',
  `biz_id` bigint DEFAULT NULL COMMENT '业务ID',
  `jump_type` varchar(30) DEFAULT NULL COMMENT '跳转类型',
  `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转地址',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读（0未读 1已读）',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `push_time` datetime DEFAULT NULL COMMENT '推送时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='消息通知表';

-- ========================================================
-- 九、用户配置模块
-- ========================================================

-- 18. 用户配置表
DROP TABLE IF EXISTS `biz_user_config`;
CREATE TABLE `biz_user_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `anonymous_projection` tinyint DEFAULT '1' COMMENT '是否允许匿名投影（0否 1是）',
  `ai_personalized_analysis` tinyint DEFAULT '1' COMMENT '是否开启个性化分析（0否 1是）',
  `save_origin_asset` tinyint DEFAULT '1' COMMENT '是否保留原始素材（0否 1是）',
  `daily_remind_enabled` tinyint DEFAULT '0' COMMENT '是否开启每日提醒（0否 1是）',
  `daily_remind_time` varchar(10) DEFAULT NULL COMMENT '每日提醒时间，如21:00',
  `weekly_report_enabled` tinyint DEFAULT '1' COMMENT '是否开启周报提醒（0否 1是）',
  `language` varchar(20) DEFAULT 'zh-CN' COMMENT '语言',
  `theme_mode` varchar(20) DEFAULT 'auto' COMMENT '主题模式',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户配置表';

-- ========================================================
-- 十、初始化数据
-- ========================================================

-- 初始化情绪标签
INSERT INTO `biz_tag` (`tag_name`, `tag_code`, `tag_type`, `sort_order`, `status`, `create_time`, `create_by`, `del_flag`, `remark`) VALUES
('开心', 'happy', 'emotion', 1, 1, NOW(), 'system', 0, '初始化'),
('焦虑', 'anxious', 'emotion', 2, 1, NOW(), 'system', 0, '初始化'),
('疲惫', 'tired', 'emotion', 3, 1, NOW(), 'system', 0, '初始化'),
('平静', 'calm', 'emotion', 4, 1, NOW(), 'system', 0, '初始化'),
('委屈', 'wronged', 'emotion', 5, 1, NOW(), 'system', 0, '初始化'),
('期待', 'expect', 'emotion', 6, 1, NOW(), 'system', 0, '初始化'),
('孤独', 'lonely', 'emotion', 7, 1, NOW(), 'system', 0, '初始化'),
('烦躁', 'irritable', 'emotion', 8, 1, NOW(), 'system', 0, '初始化'),
('悲伤', 'sad', 'emotion', 9, 1, NOW(), 'system', 0, '初始化'),
('温暖', 'warm', 'emotion', 10, 1, NOW(), 'system', 0, '初始化'),
('迷茫', 'confused', 'emotion', 11, 1, NOW(), 'system', 0, '初始化'),
('充满希望', 'hopeful', 'emotion', 12, 1, NOW(), 'system', 0, '初始化');

-- 初始化场景标签
INSERT INTO `biz_scene` (`scene_name`, `scene_code`, `icon`, `sort_order`, `status`, `create_time`, `create_by`, `del_flag`, `remark`) VALUES
('工作', 'work', '/icons/work.png', 1, 1, NOW(), 'system', 0, '初始化'),
('学习', 'study', '/icons/study.png', 2, 1, NOW(), 'system', 0, '初始化'),
('家庭', 'family', '/icons/family.png', 3, 1, NOW(), 'system', 0, '初始化'),
('社交', 'social', '/icons/social.png', 4, 1, NOW(), 'system', 0, '初始化'),
('感情', 'love', '/icons/love.png', 5, 1, NOW(), 'system', 0, '初始化'),
('睡眠', 'sleep', '/icons/sleep.png', 6, 1, NOW(), 'system', 0, '初始化'),
('健康', 'health', '/icons/health.png', 7, 1, NOW(), 'system', 0, '初始化'),
('娱乐', 'entertainment', '/icons/entertainment.png', 8, 1, NOW(), 'system', 0, '初始化'),
('运动', 'sport', '/icons/sport.png', 9, 1, NOW(), 'system', 0, '初始化'),
('独处', 'alone', '/icons/alone.png', 10, 1, NOW(), 'system', 0, '初始化');

-- 结束