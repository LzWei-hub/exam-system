-- 使用 INSERT IGNORE 避免重复插入（推荐）
INSERT IGNORE INTO sys_user (username, password, real_name, role, email, avatar, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCrmbXdWDFbeMjQ8QroS.7xv6J5oNtDpOgvYjKb1B.y', '系统管理员', 'ADMIN', 'admin@example.com', 'avatar/admin.jpg', 1),
('teacher1', '$2a$10$rY7W5U2GkZxJvLw9qMz0UeY3XoQgS0b1NkFv5JtLm3cR7VdKv1B6C', '张老师', 'TEACHER', 'zhang@example.com', 'avatar/t1.jpg', 1),
('teacher2', '$2a$10$h5T7WpQeF2sV8mR3YbL0EeZvB1NkC4M6D7G8J9K0L2I3O5P1A9S', '李老师', 'TEACHER', 'li@example.com', 'avatar/t2.jpg', 1),
('student1', '$2a$10$yT3W5U7GkZxJvLw9qMz0UeY3XoQgS0b1NkFv5JtLm3cR7VdKv1B6C', '王同学', 'STUDENT', 'wang@example.com', 'avatar/s1.jpg', 1),
('student2', '$2a$10$qY7W5U2GkZxJvLw9qMz0UeY3XoQgS0b1NkFv5JtLm3cR7VdKv1B6C', '赵同学', 'STUDENT', 'zhao@example.com', 'avatar/s2.jpg', 1);

-- 插入科目数据（两级结构）
SET FOREIGN_KEY_CHECKS=0;
INSERT INTO exam_subject (name, parent_id, level) VALUES
('数学', 0, 1),
('语文', 0, 1),
('代数', 1, 2),
('几何', 1, 2),
('现代文阅读', 2, 2);
SET FOREIGN_KEY_CHECKS=1;

-- 插入试题数据（覆盖所有题型）
INSERT INTO exam_question (subject_id, question_type, content, options, answer, analysis, difficulty, score, creator_id) VALUES
(1, 'SINGLE', '1+1等于多少？', '["A.1", "B.2", "C.3", "D.4"]', 'B', '基础数学题', 1, 2.0, 2),
(1, 'MULTI', '以下哪些是质数？', '["A.2", "B.3", "C.4", "D.5"]', '["A","B","D"]', '质数判断', 3, 3.0, 3),
(2, 'JUDGE', '《红楼梦》的作者是曹雪芹', '["正确", "错误"]', '正确', '文学常识', 2, 1.0, 2),
(3, 'FILL', '一元二次方程的解为__', NULL, 'x=±1', '求根公式', 4, 5.0, 3),
(2, 'SINGLE', '鲁迅的代表作是？', '["A.呐喊", "B.围城", "C.平凡的世界"]', 'A', '文学常识', 2, 2.0, 2);

-- 插入试卷数据（包含题目元数据）
INSERT INTO exam_paper (title, subject_id, total_score, time_limit, question_data, creator_id, exam_start, exam_end) VALUES
('数学期中考试', 1, 100.0, 90, '[{"question_id":1,"score":20},{"question_id":2,"score":30}]', 2, '2023-10-10 09:00:00', '2023-10-10 10:30:00'),
('语文期末考试', 2, 120.0, 120, '[{"question_id":3,"score":40},{"question_id":5,"score":20}]', 3, '2023-12-15 14:00:00', '2023-12-15 16:00:00'),
('代数小测', 3, 50.0, 45, '[{"question_id":4,"score":50}]', 2, '2023-11-01 10:00:00', '2023-11-01 10:45:00'),
('几何测试', 1, 80.0, 60, '[{"question_id":1,"score":80}]', 3, NULL, NULL),
('现代文练习', 2, 60.0, 45, '[{"question_id":5,"score":60}]', 2, '2023-09-20 09:00:00', '2023-09-20 09:45:00');

-- 插入考试记录
INSERT INTO exam_record (user_id, paper_id, start_time, submit_time, answer_snapshot, auto_score, manual_score) VALUES
(4, 1, '2023-10-10 09:05:00', '2023-10-10 10:00:00', '{"1":"B","2":["A","B"]}', 45.0, 5.0),
(5, 2, '2023-12-15 14:10:00', '2023-12-15 15:50:00', '{"3":"正确","5":"A"}', 55.0, 5.0),
(4, 3, '2023-11-01 10:02:00', NULL, NULL, 0.0, 0.0),
(5, 4, '2023-11-05 09:15:00', '2023-11-05 09:50:00', '{"1":"C"}', 0.0, 0.0),
(4, 5, '2023-09-20 09:03:00', '2023-09-20 09:40:00', '{"5":"A"}', 60.0, 0.0);

-- 插入错题本记录
INSERT INTO exam_wrong_book (user_id, question_id, wrong_count) VALUES
(4, 1, 3),
(5, 3, 2),
(4, 2, 1),
(5, 5, 4),
(4, 4, 2);