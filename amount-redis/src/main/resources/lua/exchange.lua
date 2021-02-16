---
--- 兑换操作: 金豆兑换金币
---
--- @keys
---   1: redis 金币 key
---   2: redis 金豆 key
---
--- @argv
---   1: 用户
---   2: 扣除的金豆
---   3: 增加的金币
---

local gold_coin_key = KEYS[1]
local gold_bean_key = KEYS[2]

local user          = ARGV[1]
local deduction     = ARGV[2]
local increase      = ARGV[3]

local remain = redis.call('HGET', gold_bean_key, user)
if (not remain or tonumber(remain) < tonumber(deduction))
then
  return -1
end

redis.call('HINCRBY', gold_bean_key, user, deduction)
redis.call('HINCRBY', gold_coin_key, user, increase)

return 1