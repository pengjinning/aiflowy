SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_ai_bot
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot`;
CREATE TABLE `tb_ai_bot`  (
                              `id` bigint UNSIGNED NOT NULL COMMENT '主键ID',
                              `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
                              `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                              `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
                              `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                              `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
                              `llm_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'LLM ID',
                              `llm_options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'LLM选项',
                              `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选项',
                              `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `created_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建者ID',
                              `modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
                              `modified_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '修改者ID',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for tb_ai_bot_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_knowledge`;
CREATE TABLE `tb_ai_bot_knowledge`  (
                                        `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
                                        `bot_id` bigint UNSIGNED NULL DEFAULT NULL,
                                        `knowledge_id` bigint UNSIGNED NULL DEFAULT NULL,
                                        `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot_llm
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_llm`;
CREATE TABLE `tb_ai_bot_llm`  (
                                  `id` bigint UNSIGNED NOT NULL,
                                  `bot_id` bigint UNSIGNED NULL DEFAULT NULL,
                                  `llm_id` bigint UNSIGNED NULL DEFAULT NULL,
                                  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_message`;
CREATE TABLE `tb_ai_bot_message`  (
                                      `id` bigint UNSIGNED NOT NULL,
                                      `bot_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'Bot ID',
                                      `account_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联的账户ID',
                                      `session_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回话ID',
                                      `role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                      `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                      `image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                      `prompt_tokens` int NULL DEFAULT NULL,
                                      `completion_tokens` int NULL DEFAULT NULL,
                                      `total_tokens` int NULL DEFAULT NULL,
                                      `functions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '方法定义',
                                      `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                      `created` datetime NULL DEFAULT NULL,
                                      `modified` datetime NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `bot_id`(`bot_id`) USING BTREE,
                                      INDEX `account_id`(`account_id`) USING BTREE,
                                      INDEX `session_id`(`session_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Bot 消息记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot_plugins
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_plugins`;
CREATE TABLE `tb_ai_bot_plugins`  (
                                      `id` bigint UNSIGNED NOT NULL,
                                      `bot_id` bigint UNSIGNED NULL DEFAULT NULL,
                                      `plugin_id` bigint UNSIGNED NULL DEFAULT NULL,
                                      `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_bot_workflow
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_bot_workflow`;
CREATE TABLE `tb_ai_bot_workflow`  (
                                       `id` bigint UNSIGNED NOT NULL,
                                       `bot_id` bigint UNSIGNED NULL DEFAULT NULL,
                                       `workflow_id` bigint UNSIGNED NULL DEFAULT NULL,
                                       `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_chat_message`;
CREATE TABLE `tb_ai_chat_message`  (
                                       `id` bigint UNSIGNED NOT NULL,
                                       `topic_id` bigint UNSIGNED NULL DEFAULT NULL,
                                       `role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                       `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                       `image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                       `prompt_tokens` int NULL DEFAULT NULL,
                                       `completion_tokens` int NULL DEFAULT NULL,
                                       `total_tokens` int NULL DEFAULT NULL,
                                       `tools` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                       `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                       `created` datetime NULL DEFAULT NULL,
                                       `modified` datetime NULL DEFAULT NULL,
                                       PRIMARY KEY (`id`) USING BTREE,
                                       INDEX `topic_id`(`topic_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 消息记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_chat_topic
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_chat_topic`;
CREATE TABLE `tb_ai_chat_topic`  (
                                     `id` bigint UNSIGNED NOT NULL,
                                     `account_id` bigint UNSIGNED NULL DEFAULT NULL,
                                     `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `created` datetime NULL DEFAULT NULL,
                                     `modified` datetime NULL DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `account_id`(`account_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 话题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_document
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_document`;
CREATE TABLE `tb_ai_document`  (
                                   `id` bigint UNSIGNED NOT NULL,
                                   `knowledge_id` bigint UNSIGNED NOT NULL COMMENT '知识库ID',
                                   `document_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档类型 pdf/word/aieditor 等',
                                   `document_path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档路径',
                                   `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
                                   `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
                                   `content_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容类型',
                                   `slug` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'URL 别名',
                                   `order_no` int NULL DEFAULT NULL COMMENT '排序序号',
                                   `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他配置项',
                                   `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `created_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
                                   `modified` datetime NULL DEFAULT NULL COMMENT '最后的修改时间',
                                   `modified_by` bigint NULL DEFAULT NULL COMMENT '最后的修改人的ID',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `knowledge_id`(`knowledge_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文档' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_document_chunk
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_document_chunk`;
CREATE TABLE `tb_ai_document_chunk`  (
                                         `id` bigint UNSIGNED NOT NULL,
                                         `document_id` bigint UNSIGNED NOT NULL COMMENT '文档ID',
                                         `knowledge_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '知识库ID',
                                         `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '分块内容',
                                         `sorting` int UNSIGNED NULL DEFAULT NULL COMMENT '分割顺序',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_document_history
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_document_history`;
CREATE TABLE `tb_ai_document_history`  (
                                           `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
                                           `document_id` bigint NULL DEFAULT NULL COMMENT '修改的文档ID',
                                           `old_title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '旧标题',
                                           `new_title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '新标题',
                                           `old_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '旧内容',
                                           `new_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '新内容',
                                           `old_document_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '旧的文档类型',
                                           `new_document_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '新的额文档类型',
                                           `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                           `created_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for tb_ai_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_knowledge`;
CREATE TABLE `tb_ai_knowledge`  (
                                    `id` bigint UNSIGNED NOT NULL,
                                    `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
                                    `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                                    `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICON',
                                    `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
                                    `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                    `slug` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'URL 别名',
                                    `vector_store_enable` tinyint(1) NULL DEFAULT NULL COMMENT '是否启用向量存储',
                                    `vector_store_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量数据库类型',
                                    `vector_store_collection` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '向量数据库集合',
                                    `vector_store_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '向量数据库配置',
                                    `vector_embed_llm_id` bigint NULL DEFAULT NULL COMMENT 'Embedding 模型ID',
                                    `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                    `created_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建用户ID',
                                    `modified` datetime NULL DEFAULT NULL COMMENT '最后一次修改时间',
                                    `modified_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '最后一次修改用户ID',
                                    `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他配置',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_llm
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_llm`;
CREATE TABLE `tb_ai_llm`  (
                              `id` bigint UNSIGNED NOT NULL,
                              `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
                              `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                              `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题或名称',
                              `brand` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
                              `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICON',
                              `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                              `support_chat` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持对话',
                              `support_function_calling` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持方法调用',
                              `support_embed` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持向量化',
                              `support_reranker` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持重排',
                              `support_text_to_image` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持文字生成图片',
                              `support_image_to_image` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持图片生成图片',
                              `support_text_to_audio` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持文字生成语音',
                              `support_audio_to_audio` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持语音生成语音',
                              `support_text_to_video` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持文字生成视频',
                              `support_image_to_video` tinyint(1) NULL DEFAULT NULL COMMENT '是否支持图片生成视频',
                              `llm_endpoint` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '大模型请求地址',
                              `llm_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '大模型名称',
                              `llm_api_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '大模型 API KEY',
                              `llm_extra_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '大模型其他属性配置',
                              `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他配置内容',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_plugins
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_plugins`;
CREATE TABLE `tb_ai_plugins`  (
                                  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
                                  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                                  `plugin_type` tinyint NOT NULL DEFAULT 1 COMMENT '插件类型',
                                  `plugin_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '插件名称',
                                  `plugin_desc` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '插件描述',
                                  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '插件配置',
                                  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
                                  `created` datetime NOT NULL COMMENT '创建时间',
                                  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
                                  `modified` datetime NOT NULL COMMENT '修改时间',
                                  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
                                  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
                                  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '插件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_ai_workflow
-- ----------------------------
DROP TABLE IF EXISTS `tb_ai_workflow`;
CREATE TABLE `tb_ai_workflow`  (
                                   `id` bigint UNSIGNED NOT NULL COMMENT 'ID 主键',
                                   `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
                                   `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                                   `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
                                   `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                   `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICON',
                                   `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '工作流设计的 JSON 内容',
                                   `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `created_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
                                   `modified` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
                                   `modified_by` bigint UNSIGNED NULL DEFAULT NULL COMMENT '最后修改的人',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_account
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_account`;
CREATE TABLE `tb_sys_account`  (
                                   `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                   `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
                                   `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                                   `login_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
                                   `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
                                   `account_type` tinyint NOT NULL DEFAULT 0 COMMENT '账户类型',
                                   `nickname` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '昵称',
                                   `mobile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机电话',
                                   `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '邮件',
                                   `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '账户头像',
                                   `data_scope` int NULL DEFAULT 1 COMMENT '数据权限类型',
                                   `dept_id_list` json NULL COMMENT '自定义部门权限',
                                   `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
                                   `created` datetime NOT NULL COMMENT '创建时间',
                                   `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
                                   `modified` datetime NOT NULL COMMENT '修改时间',
                                   `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
                                   `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
                                   `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX `uni_login_name`(`login_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_account_position
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_account_position`;
CREATE TABLE `tb_sys_account_position`  (
                                            `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                            `account_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                            `position_id` bigint UNSIGNED NOT NULL COMMENT '职位ID',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-职位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_account_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_account_role`;
CREATE TABLE `tb_sys_account_role`  (
                                        `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                        `account_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
                                        `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_dept`;
CREATE TABLE `tb_sys_dept`  (
                                `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                                `parent_id` bigint UNSIGNED NOT NULL COMMENT '父级ID',
                                `ancestors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '父级部门ID集合',
                                `dept_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
                                `dept_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门编码',
                                `sort_no` int NULL DEFAULT 0 COMMENT '排序',
                                `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
                                `created` datetime NOT NULL COMMENT '创建时间',
                                `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
                                `modified` datetime NOT NULL COMMENT '修改时间',
                                `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
                                `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
                                `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_dict`;
CREATE TABLE `tb_sys_dict`  (
                                `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据字典名称',
                                `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '字典编码',
                                `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '字典描述或备注',
                                `dict_type` tinyint NULL DEFAULT NULL COMMENT '字典类型 1 自定义字典、2 数据表字典、 3 枚举类字典、 4 系统字典（自定义 DictLoader）',
                                `sort_no` int NULL DEFAULT NULL COMMENT '排序编号',
                                `status` tinyint NULL DEFAULT NULL COMMENT '是否启用',
                                `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展字典  存放 json',
                                `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `key`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_dict_item`;
CREATE TABLE `tb_sys_dict_item`  (
                                     `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                     `dict_id` bigint UNSIGNED NOT NULL COMMENT '归属哪个字典',
                                     `text` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '名称或内容',
                                     `value` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '值',
                                     `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                                     `sort_no` int NOT NULL DEFAULT 0 COMMENT '排序',
                                     `css_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'css样式内容',
                                     `css_class` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'css样式类名',
                                     `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
                                     `status` tinyint NULL DEFAULT 0 COMMENT '状态',
                                     `created` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                     `modified` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据字典内容' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_log`;
CREATE TABLE `tb_sys_log`  (
                               `id` bigint UNSIGNED NOT NULL,
                               `account_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '操作人',
                               `action_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作名称',
                               `action_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作的类型',
                               `action_class` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作涉及的类',
                               `action_method` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作涉及的方法',
                               `action_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作涉及的 URL 地址',
                               `action_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作涉及的用户 IP 地址',
                               `action_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作请求参数',
                               `action_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作请求body',
                               `status` tinyint NULL DEFAULT NULL COMMENT '操作状态 1 成功 9 失败',
                               `created` datetime NULL DEFAULT NULL COMMENT '操作时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_menu`;
CREATE TABLE `tb_sys_menu`  (
                                `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                `parent_id` bigint UNSIGNED NOT NULL COMMENT '父菜单id',
                                `menu_type` int NOT NULL COMMENT '菜单类型',
                                `menu_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单标题',
                                `menu_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单url',
                                `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '组件路径',
                                `menu_icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '图标/图片地址',
                                `is_show` int NOT NULL DEFAULT 1 COMMENT '是否显示',
                                `permission_tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '权限标识',
                                `sort_no` int NULL DEFAULT 0 COMMENT '排序',
                                `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
                                `created` datetime NOT NULL COMMENT '创建时间',
                                `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
                                `modified` datetime NOT NULL COMMENT '修改时间',
                                `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
                                `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
                                `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_option
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_option`;
CREATE TABLE `tb_sys_option`  (
                                  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                                  `key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置KEY',
                                  `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置内容',
                                  INDEX `uni_key`(`tenant_id`, `key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_sys_position
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_position`;
CREATE TABLE `tb_sys_position`  (
                                    `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                    `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                                    `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
                                    `position_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
                                    `position_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '岗位编码',
                                    `sort_no` int NULL DEFAULT 0 COMMENT '排序',
                                    `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
                                    `created` datetime NOT NULL COMMENT '创建时间',
                                    `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
                                    `modified` datetime NOT NULL COMMENT '修改时间',
                                    `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
                                    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
                                    `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '职位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_role`;
CREATE TABLE `tb_sys_role`  (
                                `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
                                `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
                                `role_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色标识',
                                `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
                                `created` datetime NOT NULL COMMENT '创建时间',
                                `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
                                `modified` datetime NOT NULL COMMENT '修改时间',
                                `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
                                `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
                                `is_deleted` tinyint NULL DEFAULT 0 COMMENT '删除标识',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `uni_tenant_role`(`tenant_id`, `role_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_role_menu`;
CREATE TABLE `tb_sys_role_menu`  (
                                     `id` bigint UNSIGNED NOT NULL COMMENT '主键',
                                     `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
                                     `menu_id` bigint UNSIGNED NOT NULL COMMENT '菜单ID',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色-菜单表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
