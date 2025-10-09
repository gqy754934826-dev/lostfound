-- 创建联系人表
CREATE TABLE IF NOT EXISTS `user_contact` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '联系人关系ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `contact_user_id` BIGINT NOT NULL COMMENT '联系人用户ID',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_contact` (`user_id`, `contact_user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_contact_user_id` (`contact_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户联系人表';