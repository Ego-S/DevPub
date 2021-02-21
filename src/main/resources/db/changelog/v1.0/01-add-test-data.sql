INSERT INTO users
(email, is_moderator, name, password, reg_time)
VALUES
('moderator@gmail.com', 1, 'moderator', '$2y$12$FdXdOhOt3C1pRggs57eDoeQA6N8IV94xGyasCf4IfzBpUMO5w5WSO', '2020-01-01 12:00:00'),
('user@gmail.com', 0, 'user', '$2y$12$cu5W7qEzS6BZ/mz2I.TNFuPGo57drHWf59I17dKmTcCEULgyrnxiu', '2020-01-01 12:00:00')
GO
--for login use
--moderator@gmail.com: moderator
--user@gmail.com: useruser


INSERT INTO posts
(is_active, moderation_status, time, text, title, user_id, view_count)
VALUES
(1, 'ACCEPTED', '2021-01-28 12:30:10', 'Test text of the post number 1', 'Test post 1', 1, 12),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 2', 'Test post 2', 2, 22),
(1, 'NEW', '2021-01-28 12:30:00', 'Test text of the post number 3', 'Test post 3', 1, 10),
(0, 'DECLINED', '2021-01-28 12:30:00', 'Test text of the post number 4', 'Test post 4', 2, 8),
(1, 'ACCEPTED', '2022-01-12 12:30:00', 'Test text of the post number 5', 'Test post 5', 1, 0),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 1', 'Test post 1', 1, 12),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 2', 'Test post 2', 2, 22),
(1, 'NEW', '2021-01-28 12:30:00', 'Test text of the post number 3', 'Test post 3', 1, 10),
(0, 'DECLINED', '2021-01-28 12:30:00', 'Test text of the post number 4', 'Test post 4', 2, 8),
(1, 'ACCEPTED', '2022-01-28 12:30:00', 'Test text of the post number 5', 'Test post 5', 1, 0),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 1', 'Test post 1', 1, 12),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 2', 'Test post 2', 2, 22),
(1, 'NEW', '2021-01-28 12:30:00', 'Test text of the post number 3', 'Test post 3', 1, 10),
(0, 'DECLINED', '2021-01-28 12:30:00', 'Test text of the post number 4', 'Test post 4', 2, 8),
(1, 'ACCEPTED', '2022-01-28 12:30:00', 'Test text of the post number 5', 'Test post 5', 1, 0),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 1', 'Test post 1', 1, 12),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 2', 'Test post 2', 2, 22),
(1, 'NEW', '2021-01-28 12:30:00', 'Test text of the post number 3', 'Test post 3', 1, 10),
(0, 'DECLINED', '2021-01-28 12:30:00', 'Test text of the post number 4', 'Test post 4', 2, 8),
(1, 'ACCEPTED', '2022-01-28 12:30:00', 'Test text of the post number 5', 'Test post 5', 1, 0),
(1, 'ACCEPTED', '2021-01-22 12:30:00', 'Test text of the post number 1', 'Test post 1', 1, 12),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 2', 'Test post 2', 2, 22),
(1, 'NEW', '2021-01-28 12:30:00', 'Test text of the post number 3', 'Test post 3', 1, 10),
(0, 'DECLINED', '2021-01-28 12:30:00', 'Test text of the post number 4', 'Test post 4', 2, 8),
(1, 'ACCEPTED', '2022-01-28 12:30:00', 'Test text of the post number 5', 'Test post 5', 1, 0),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 1', 'Test post 1', 1, 12),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test text of the post number 2', 'Test post 2', 2, 22),
(1, 'NEW', '2021-01-28 12:30:00', 'Test text of the post number 3', 'Test post 3', 1, 10),
(0, 'DECLINED', '2021-01-28 12:30:00', 'Test text of the post number 4', 'Test post 4', 2, 8),
(1, 'ACCEPTED', '2022-01-28 12:30:00', 'Test text of the post number 5', 'Test post 5', 1, 0),
(1, 'ACCEPTED', '2021-01-22 12:30:00', 'Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. 1000000000000000000', 'Test post 1', 1, 12),
(1, 'ACCEPTED', '2021-01-28 12:30:00', 'Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. 1000000000000000000', 'Test post 2', 2, 22),
(1, 'NEW', '2021-01-28 12:30:00', 'Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. 1000000000000000000', 'Test post 3', 1, 10),
(0, 'DECLINED', '2021-01-28 12:30:00', 'Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. 1000000000000000000', 'Test post 4', 2, 8),
(1, 'ACCEPTED', '2022-01-28 12:30:00', 'Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. Test Text <HTML> long text. 1000000000000000000', 'Test post 5', 1, 0)
GO


INSERT INTO post_votes
(user_id, post_id, time, value)
VALUES
(1, 1, '2021-01-28 13:00:00', 1),
(1, 2, '2021-01-28 13:00:00', 1),
(2, 1, '2021-01-28 13:00:00', -1),
(1, 2, '2021-01-28 13:00:00', 1),
(2, 1, '2021-01-28 13:00:00', -1),
(1, 2, '2021-01-28 13:00:00', 1),
(2, 1, '2021-01-28 13:00:00', -1)
GO


INSERT INTO post_comments
(post_id, user_id, time, text)
VALUES
(1, 1, '2021-01-28 13:00:00', 'first comment'),
(1, 2, '2021-01-28 13:00:00', 'second comment'),
(2, 1, '2021-01-28 13:00:00', 'first comment')
GO


INSERT INTO tags
(name)
VALUES
('test'),
('1')
GO


INSERT INTO tag2post
(post_id, tag_id)
VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (1, 2)
GO