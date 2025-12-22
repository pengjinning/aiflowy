SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_workflow_exec_record
-- ----------------------------
CREATE TABLE `tb_workflow_exec_record`
(
    `id`            bigint unsigned NOT NULL COMMENT '主键',
    `exec_key`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行标识',
    `workflow_id`   bigint unsigned NOT NULL COMMENT '工作流ID',
    `title`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '标题',
    `description`   varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT '描述',
    `input`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '输入',
    `output`        text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '输出',
    `workflow_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '工作流执行时的配置',
    `start_time`    datetime(3) NOT NULL COMMENT '开始时间',
    `end_time`      datetime(3) DEFAULT NULL COMMENT '结束时间',
    `tokens`        bigint unsigned DEFAULT NULL COMMENT '消耗总token',
    `status`        tinyint                                                       NOT NULL DEFAULT '0' COMMENT '数据状态',
    `created_key`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci           DEFAULT NULL COMMENT '执行人标识[有可能是用户|外部|定时任务等情况]',
    `created_by`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci           DEFAULT NULL COMMENT '执行人',
    `error_info`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '错误信息',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uni_exec_key` (`exec_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='工作流执行记录';

-- ----------------------------
-- Table structure for tb_workflow_record_step
-- ----------------------------
CREATE TABLE `tb_workflow_record_step`
(
    `id`         bigint unsigned NOT NULL COMMENT '主键',
    `record_id`  bigint unsigned NOT NULL COMMENT '执行记录ID',
    `exec_key`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行标识',
    `node_id`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点ID',
    `node_name`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '节点名称',
    `input`      longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '输入',
    `output`     longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '输出',
    `node_data`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '节点信息',
    `start_time` datetime(3) NOT NULL COMMENT '开始时间',
    `end_time`   datetime(3) DEFAULT NULL COMMENT '结束时间',
    `tokens`     bigint unsigned DEFAULT NULL COMMENT '消耗总token',
    `status`     tinyint                                                       NOT NULL DEFAULT '0' COMMENT '数据状态',
    `error_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '错误信息',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uni_exec` (`exec_key`) USING BTREE,
    KEY          `idx_record_id` (`record_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='执行记录步骤';

-- ----------------------------
-- Table structure for tb_bot_category
-- ----------------------------
CREATE TABLE `tb_bot_category`
(
    `id`            bigint unsigned NOT NULL COMMENT '主键',
    `category_name` varchar(50) NOT NULL COMMENT '分类名称',
    `sort_no`       int                  DEFAULT '0' COMMENT '排序',
    `created`       datetime    NOT NULL COMMENT '创建时间',
    `created_by`    bigint unsigned NOT NULL COMMENT '创建者',
    `modified`      datetime    NOT NULL COMMENT '修改时间',
    `modified_by`   bigint unsigned NOT NULL COMMENT '修改者',
    `status`        int         NOT NULL DEFAULT '0' COMMENT '数据状态',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='bot分类';

-- ----------------------------
-- Table structure for tb_bot_recently_used
-- ----------------------------
CREATE TABLE `tb_bot_recently_used`
(
    `id`         bigint unsigned NOT NULL COMMENT '主键',
    `bot_id`     bigint unsigned NOT NULL COMMENT 'botId',
    `created`    datetime NOT NULL COMMENT '创建时间',
    `created_by` bigint unsigned NOT NULL COMMENT '创建者',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='最近使用';

ALTER TABLE `tb_bot`
    ADD COLUMN `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '分类ID' AFTER `status`;

-- 删除旧的模型能力字段
ALTER TABLE tb_llm
DROP
COLUMN support_chat,
    DROP
COLUMN support_function_calling,
    DROP
COLUMN support_embed,
    DROP
COLUMN support_reranker,
    DROP
COLUMN support_text_to_image,
    DROP
COLUMN support_image_to_image,
    DROP
COLUMN support_text_to_audio,
    DROP
COLUMN support_audio_to_audio,
    DROP
COLUMN support_text_to_video,
    DROP
COLUMN support_image_to_video;

-- 添加新的字段
ALTER TABLE tb_llm
    ADD COLUMN group_name varchar(255) NULL COMMENT '分组名称' AFTER options,
    ADD COLUMN model_type varchar(255) NULL COMMENT '模型类型' AFTER group_name,
    ADD COLUMN provider varchar(255) NULL COMMENT '供应商' AFTER model_type,
    ADD COLUMN support_reasoning tinyint(1) NULL COMMENT '是否支持推理' AFTER provider,
    ADD COLUMN support_tool tinyint(1) NULL COMMENT '是否支持工具' AFTER support_reasoning,
    ADD COLUMN support_embedding tinyint(1) NULL COMMENT '是否支持嵌入' AFTER support_tool,
    ADD COLUMN support_rerank tinyint(1) NULL COMMENT '是否支持重排' AFTER support_embedding;

-- 添加关联/状态字段
ALTER TABLE tb_llm
    ADD COLUMN provider_id bigint UNSIGNED NULL COMMENT '供应商id' AFTER support_rerank,
    ADD COLUMN added tinyint(1) NULL COMMENT '是否添加' AFTER provider_id,
    ADD COLUMN can_delete tinyint(1) NULL COMMENT '是否能删除' AFTER added,
    ADD COLUMN support_free tinyint(1) NULL COMMENT '是否免费' AFTER can_delete;

-- 创建索引
CREATE INDEX idx_tb_llm_provider_id ON tb_llm (provider_id);
CREATE INDEX idx_tb_llm_model_type ON tb_llm (model_type);
CREATE INDEX idx_tb_llm_added ON tb_llm (added);


SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_llm_provider
-- ----------------------------
DROP TABLE IF EXISTS `tb_llm_provider`;
CREATE TABLE `tb_llm_provider`
(
    `id`            bigint(0) UNSIGNED NOT NULL COMMENT 'id',
    `provider_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商名称',
    `created`       datetime(0) NOT NULL COMMENT '创建时间',
    `created_by`    bigint(0) UNSIGNED NOT NULL COMMENT '创建者',
    `modified`      datetime(0) NOT NULL COMMENT '修改时间',
    `modified_by`   bigint(0) UNSIGNED NOT NULL COMMENT '修改者',
    `icon`          varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
    `api_key`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'apiKey',
    `end_point`     varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'endPoint',
    `chat_path`     varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对话地址',
    `embed_path`    varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量地址',
    `provider`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商',
    `rerank_path`   varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '重排路径',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

INSERT INTO `tb_llm_provider`(`id`, `provider_name`, `created`, `created_by`, `modified`, `modified_by`, `icon`, `api_key`, `end_point`, `chat_path`, `embed_path`, `provider`, `rerank_path`) VALUES (358906187162456064, 'Gitee', '2025-12-17 22:26:03', 1, '2025-12-17 22:26:03', 1, '', '', 'https://ai.gitee.com', '/v1/chat/completions', '/v1/embeddings', 'gitee', NULL);
INSERT INTO `tb_llm_provider`(`id`, `provider_name`, `created`, `created_by`, `modified`, `modified_by`, `icon`, `api_key`, `end_point`, `chat_path`, `embed_path`, `provider`, `rerank_path`) VALUES (359109019710914560, '百度千帆', '2025-12-18 11:52:02', 1, '2025-12-18 11:52:02', 1, '', '', 'https://qianfan.baidubce.com', '/v2/chat/completions', '/v2/embeddings', 'baidu', '');
INSERT INTO `tb_llm_provider`(`id`, `provider_name`, `created`, `created_by`, `modified`, `modified_by`, `icon`, `api_key`, `end_point`, `chat_path`, `embed_path`, `provider`, `rerank_path`) VALUES (359110565693620224, '火山引擎', '2025-12-18 11:58:11', 1, '2025-12-18 11:58:11', 1, '', '', 'https://ark.cn-beijing.volces.com', '/api/v3/chat/completions', '/api/v3/embeddings', 'volcengine', '');
INSERT INTO `tb_llm_provider`(`id`, `provider_name`, `created`, `created_by`, `modified`, `modified_by`, `icon`, `api_key`, `end_point`, `chat_path`, `embed_path`, `provider`, `rerank_path`) VALUES (359110640402563072, '星火大模型', '2025-12-18 11:58:29', 1, '2025-12-18 11:58:29', 1, '', '', 'https://spark-api-open.xf-yun.com', '/v1/chat/completions', '', 'spark', '');
INSERT INTO `tb_llm_provider`(`id`, `provider_name`, `created`, `created_by`, `modified`, `modified_by`, `icon`, `api_key`, `end_point`, `chat_path`, `embed_path`, `provider`, `rerank_path`) VALUES (359110667376132096, '硅基流动', '2025-12-18 11:58:35', 1, '2025-12-18 11:58:35', 1, '', '', 'https://api.siliconflow.cn', '/v1/chat/completions', '/v1/embeddings', 'siliconlow', '');
INSERT INTO `tb_llm_provider`(`id`, `provider_name`, `created`, `created_by`, `modified`, `modified_by`, `icon`, `api_key`, `end_point`, `chat_path`, `embed_path`, `provider`, `rerank_path`) VALUES (359110690079899648, 'Ollama', '2025-12-18 11:58:40', 1, '2025-12-18 11:58:40', 1, '', '', NULL, NULL, NULL, 'ollama', '');
INSERT INTO `tb_llm_provider`(`id`, `provider_name`, `created`, `created_by`, `modified`, `modified_by`, `icon`, `api_key`, `end_point`, `chat_path`, `embed_path`, `provider`, `rerank_path`) VALUES (359111120310632448, 'DeepSeek', '2025-12-18 12:00:23', 1, '2025-12-18 12:00:23', 1, '', '', 'https://api.deepseek.com', '/compatible-mode/v1/chat/completions', '/compatible-mode/v1/embeddings', 'deepseek', '');
INSERT INTO `tb_llm_provider`(`id`, `provider_name`, `created`, `created_by`, `modified`, `modified_by`, `icon`, `api_key`, `end_point`, `chat_path`, `embed_path`, `provider`, `rerank_path`) VALUES (359111228158771200, 'Open AI', '2025-12-18 12:00:49', 1, '2025-12-18 12:00:49', 1, '', '', 'https://api.openai.com', '/v1/chat/completions', '/v1/embeddings', 'openai', '');
INSERT INTO `tb_llm_provider`(`id`, `provider_name`, `created`, `created_by`, `modified`, `modified_by`, `icon`, `api_key`, `end_point`, `chat_path`, `embed_path`, `provider`, `rerank_path`) VALUES (359111448204541952, '阿里百炼', '2025-12-18 12:01:41', 1, '2025-12-18 12:01:41', 1, '', '', 'https://dashscope.aliyuncs.com', '/compatible-mode/v1/chat/completions', '/compatible-mode/v1/embeddings', 'aliyun', '');

INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359081705581277184, 1, 1000000, 'text-embedding-v3', NULL, NULL, NULL, NULL, 'text-embedding-v3', NULL, NULL, '{\"rerankPath\":\"/api/v1/services/rerank/text-rerank/text-rerank\",\"chatPath\":\"/compatible-mode/v1/chat/completions\",\"llmEndpoint\":\"https://dashscope.aliyuncs.com\",\"embedPath\":\"/compatible-mode/v1/embeddings\"}', 'text-embedding', 'embeddingModel', 'aliyun', 0, 0, 1, 0, 359111448204541952, 0, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359159030960295936, 1, 1000000, 'DeepSeek-V3', NULL, NULL, NULL, NULL, 'DeepSeek-V3', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'DeepSeek', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359188398742933504, 1, 1000000, 'kimi-k2-instruct', NULL, NULL, NULL, NULL, 'kimi-k2-instruct', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', '文心ERNIE', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359188487121113088, 1, 1000000, 'ERNIE-X1-Turbo', NULL, NULL, NULL, NULL, 'ERNIE-X1-Turbo', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', '文心ERNIE', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359188591383121920, 1, 1000000, 'ERNIE-4.5-Turbo', NULL, NULL, NULL, NULL, 'ERNIE-4.5-Turbo', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', '文心ERNIE', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359188717564563456, 1, 1000000, 'DeepSeek-R1', NULL, NULL, NULL, NULL, 'DeepSeek-R1', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'DeepSeek', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359188823290384384, 1, 1000000, 'Qwen3-235B-A22B', NULL, NULL, NULL, NULL, 'Qwen3-235B-A22B', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359188894316728320, 1, 1000000, 'Qwen3-30B-A3B', NULL, NULL, NULL, NULL, 'Qwen3-30B-A3B', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359189040752463872, 1, 1000000, 'Qwen3-32B', NULL, NULL, NULL, NULL, 'Qwen3-32B', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359189205492142080, 1, 1000000, 'ERNIE-4.5-Turbo-VL', NULL, NULL, NULL, NULL, 'ERNIE-4.5-Turbo-VL', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', '文心ERNIE', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359189277827108864, 1, 1000000, 'Qwen2.5-VL-32B-Instruct', NULL, NULL, NULL, NULL, 'Qwen2.5-VL-32B-Instruct', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359189415073124352, 1, 1000000, 'InternVL3-78B', NULL, NULL, NULL, NULL, 'InternVL3-78B', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'InternVL3', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359189479254364160, 1, 1000000, 'InternVL3-38B', NULL, NULL, NULL, NULL, 'InternVL3-38B', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'InternVL3', 'chatModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359189562309971968, 1, 1000000, 'Qwen3-Embedding-8B', NULL, NULL, NULL, NULL, 'Qwen3-Embedding-8B', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'embeddingModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359189617381183488, 1, 1000000, 'Qwen3-Embedding-4B', NULL, NULL, NULL, NULL, 'Qwen3-Embedding-4B', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'embeddingModel', '', 0, 0, 0, 0, 358906187162456064, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359189999838793728, 1, 1000000, 'deepseek-reasoner', NULL, NULL, NULL, NULL, 'deepseek-reasoner', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'DeepSeek', 'chatModel', '', 0, 0, 0, 0, 359111120310632448, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359190580372410368, 1, 1000000, 'deepseek-chat', NULL, NULL, NULL, NULL, 'deepseek-chat', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'DeepSeek', 'chatModel', '', 0, 0, 0, 0, 359111120310632448, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359197442178207744, 1, 1000000, '通义千问-Plus', NULL, NULL, NULL, NULL, 'qwen-plus', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359197525791657984, 1, 1000000, '通义千问-Turbo', NULL, NULL, NULL, NULL, 'qwen-turbo', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359197588123209728, 1, 1000000, '通义千问-Max', NULL, NULL, NULL, NULL, 'qwen-max', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359198412522049536, 1, 1000000, 'DeepSeek-R1-Distill-Qwen-14B', NULL, NULL, NULL, NULL, 'deepseek-r1-distill-qwen-14b', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'DeepSeek', 'chatModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359198507799859200, 1, 1000000, 'DeepSeek-R1-Distill-Qwen-32B', NULL, NULL, NULL, NULL, 'deepseek-r1-distill-qwen-32b', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'DeepSeek', 'chatModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359198722971848704, 1, 1000000, '通义千问VL-Max', NULL, NULL, NULL, NULL, 'qwen-vl-max', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359198803871584256, 1, 1000000, '通义千问VL-Plus', NULL, NULL, NULL, NULL, 'qwen-vl-plus', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359198878878322688, 1, 1000000, '通义千问-QVQ-Max', NULL, NULL, NULL, NULL, 'qvq-max', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359198954921054208, 1, 1000000, '通义千问-QVQ-Plus', NULL, NULL, NULL, NULL, 'qvq-plus', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'Qwen', 'chatModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359199099737788416, 1, 1000000, '通用文本向量-v4', NULL, NULL, NULL, NULL, 'text-embedding-v4', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', '通用文本向量', 'embeddingModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359199200937955328, 1, 1000000, '通用文本向量-v3', NULL, NULL, NULL, NULL, 'text-embedding-v3', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', '通用文本向量', 'embeddingModel', '', 0, 0, 0, 0, 359111448204541952, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359250814495248384, 1, 1000000, 'o4-mini', NULL, NULL, NULL, NULL, 'o4-mini', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'O系列', 'chatModel', '', 0, 0, 0, 0, 359111228158771200, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359250916064514048, 1, 1000000, 'o3', NULL, NULL, NULL, NULL, 'o3', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'O系列', 'chatModel', '', 0, 0, 0, 0, 359111228158771200, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359250975883677696, 1, 1000000, 'o3-pro', NULL, NULL, NULL, NULL, 'o3-pro', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'O系列', 'chatModel', '', 0, 0, 0, 0, 359111228158771200, 1, NULL, 0);
INSERT INTO `tb_llm`(`id`, `dept_id`, `tenant_id`, `title`, `brand`, `icon`, `description`, `llm_endpoint`, `llm_model`, `llm_api_key`, `llm_extra_config`, `options`, `group_name`, `model_type`, `provider`, `support_reasoning`, `support_tool`, `support_embedding`, `support_rerank`, `provider_id`, `added`, `can_delete`, `support_free`) VALUES (359251041788776448, 1, 1000000, 'o3-mini', NULL, NULL, NULL, NULL, 'o3-mini', NULL, NULL, '{\"rerankPath\":\"\",\"chatPath\":\"\",\"llmEndpoint\":\"\",\"embedPath\":\"\"}', 'O系列', 'chatModel', '', 0, 0, 0, 0, 359111228158771200, 1, NULL, 0);

SET
FOREIGN_KEY_CHECKS = 1;