DELETE FROM users WHERE reg_time='2020-01-01 12:00:00'
GO


DELETE FROM posts WHERE time='2021-01-28 12:30:00' OR time='2022-01-28 12:30:00'
GO


DELETE FROM post_votes WHERE time='2021-01-28 13:00:00'
GO


DELETE FROM post_comments WHERE time='2021-01-28 13:00:00'
GO


DELETE FROM tags WHERE id=1 OR id=2
GO


DELETE FROM tag2post WHERE id<7
GO