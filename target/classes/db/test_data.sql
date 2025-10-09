-- 插入测试用户数据
INSERT INTO `user` (`username`, `password`, `real_name`, `student_no`, `phone`, `status`, `create_time`, `update_time`)
VALUES 
('user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', '2021001', '13800138001', 0, NOW(), NOW()),
('user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', '2021002', '13800138002', 0, NOW(), NOW()),
('user3', 'e10adc3949ba59abbe56e057f20f883e', '王五', '2021003', '13800138003', 0, NOW(), NOW());
-- 密码为：123456

-- 插入测试失物招领信息数据
INSERT INTO `item` (`title`, `description`, `type`, `location`, `item_time`, `status`, `user_id`, `create_time`, `update_time`)
VALUES 
-- 最近7天的数据
('丢失钱包', '黑色皮质钱包，内有身份证和银行卡', 'lost', '图书馆', DATE_SUB(NOW(), INTERVAL 1 DAY), 1, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('捡到手机', '苹果手机，黑色外壳', 'claim', '食堂', DATE_SUB(NOW(), INTERVAL 2 DAY), 1, 2, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('丢失书包', '蓝色双肩包，内有课本', 'lost', '教学楼A座', DATE_SUB(NOW(), INTERVAL 3 DAY), 1, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('捡到钥匙', '一串钥匙，有小熊挂件', 'claim', '宿舍楼下', DATE_SUB(NOW(), INTERVAL 4 DAY), 1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('丢失眼镜', '黑框眼镜，近视镜', 'lost', '操场', DATE_SUB(NOW(), INTERVAL 5 DAY), 1, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('捡到水杯', '保温杯，粉色', 'claim', '图书馆', DATE_SUB(NOW(), INTERVAL 6 DAY), 1, 3, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
('丢失U盘', '16G金士顿U盘', 'lost', '机房', NOW(), 0, 1, NOW(), NOW()),
('捡到耳机', '白色无线耳机', 'claim', '咖啡厅', NOW(), 0, 2, NOW(), NOW()),

-- 更多历史数据
('丢失学生证', '学生证丢失，姓名张三', 'lost', '教务处', DATE_SUB(NOW(), INTERVAL 10 DAY), 3, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
('捡到充电器', '苹果手机充电器', 'claim', '宿舍', DATE_SUB(NOW(), INTERVAL 12 DAY), 3, 2, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
('丢失雨伞', '黑色折叠伞', 'lost', '公交站', DATE_SUB(NOW(), INTERVAL 15 DAY), 2, 3, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
('捡到笔记本', '数学笔记本', 'claim', '教室', DATE_SUB(NOW(), INTERVAL 18 DAY), 1, 1, DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY));