DELETE FROM global_settings

GO

INSERT INTO global_settings (code, name, value)
VALUES('MULTIUSER_MODE','Многопользовательский режим','YES'),
('POST_PREMODERATION','Премодерация постов','NO'),
('STATISTIC_IS_PUBLIC','Показывать всем статистику блога','YES')

GO