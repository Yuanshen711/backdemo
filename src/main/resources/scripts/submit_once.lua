-- 限制每个账号对每个任务只能提交一次
-- KEYS[1]: submit:once:{userId}:{taskId}
-- 返回 1 表示首次提交，0 表示重复提交
if redis.call('setnx', KEYS[1], 1) == 1 then
    redis.call('expire', KEYS[1], 86400)
    return 1
end
return 0
