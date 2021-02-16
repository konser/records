---
--- 送礼操作: 减金币 加金豆
---
--- @keys
---   1: redis 金币 key
---   2: redis 金豆 key
---   3: 发送方
---   4: 消耗金币 (单人/次)
---   5: 增加金豆 (单人/次)
---   6: 免费个数(不扣除金币)
--- @argv
---   接收方列表
--- @return
---   0
---

local gold_coin_key = KEYS[1]
local gold_bean_key = KEYS[2]

local sender        = KEYS[3]
local sender_decr   = KEYS[4]
local receive_incr  = KEYS[5]

-- 校验余额
if (tonumber(sender_decr) ~= 0)
then
  local remain = redis.call('HGET', gold_coin_key, sender)
  if (not remain or tonumber(remain) < sender_decr)
  then
    return -1
  end
end

-- 减金币
if (sender_decr < 0)
then
  redis.call('HINCRBY', gold_coin_key, sender, sender_decr)
end

for i = 1, #ARGV do
  redis.call('HINCRBY', gold_bean_key, ARGV[i], receive_incr)
end

return 1