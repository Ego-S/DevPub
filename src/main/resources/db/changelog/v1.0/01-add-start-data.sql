INSERT INTO users
(email, is_moderator, name, password, reg_time)
VALUES
('moderator@gmail.com', 1, 'moderator', '$2y$12$FdXdOhOt3C1pRggs57eDoeQA6N8IV94xGyasCf4IfzBpUMO5w5WSO', '2020-01-01 12:00:00'),
('nord-tank@yandex.ru', 1, 'Egos', '$2a$12$bJlL9pPIxe7QT2mwcRUIT.6aJss9OmQmvMGqHDNuT0sZl6hn.AI0.', '2020-01-01 12:00:00'),
('user@gmail.com', 0, 'user', '$2y$12$cu5W7qEzS6BZ/mz2I.TNFuPGo57drHWf59I17dKmTcCEULgyrnxiu', '2020-01-01 12:00:00')
GO
--for login use
--moderator@gmail.com: moderator
--user@gmail.com: useruser
--emails moderator@gmail.com and user@gmail.com are fake
