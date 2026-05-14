-- AI 分析异步任务表

DROP TABLE IF EXISTS `biz_ai_analyze_task`;
CREATE TABLE `biz_ai_analyze_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_no` varchar(32) NOT NULL COMMENT '任务号',
  `record_id` bigint NOT NULL COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `task_status` tinyint NOT NULL DEFAULT '0' COMMENT '任务状态（0排队 1执行中 2成功 3失败）',
  `fail_code` varchar(64) DEFAULT NULL COMMENT '失败码',
  `fail_message` varchar(500) DEFAULT NULL COMMENT '失败信息',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '重试次数',
  `request_snapshot` longtext COMMENT '请求快照',
  `response_snapshot` longtext COMMENT '响应快照',
  `provider` varchar(50) DEFAULT NULL COMMENT '模型供应商',
  `model_name` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `model_version` varchar(50) DEFAULT NULL COMMENT '模型版本',
  `prompt_version` varchar(50) DEFAULT NULL COMMENT 'Prompt版本',
  `request_id` varchar(100) DEFAULT NULL COMMENT '请求链路ID',
  `analysis_cost_ms` int DEFAULT NULL COMMENT '分析耗时毫秒',
  `queued_at` datetime DEFAULT NULL COMMENT '排队时间',
  `started_at` datetime DEFAULT NULL COMMENT '开始执行时间',
  `finished_at` datetime DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标记（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_no` (`task_no`),
  KEY `idx_record_user` (`record_id`,`user_id`),
  KEY `idx_status_create` (`task_status`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI分析任务表';
