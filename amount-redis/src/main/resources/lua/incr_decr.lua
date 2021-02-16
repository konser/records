---
--- @keys
---  1: 货币KEY
---
--- @argv
---  1: 用户
---  2: 数值
---

local currency_key      = KEYS[1]

local user              = ARGV[1]
local number            = ARGV[2]

if (tonumber(number) < 0)
then
  local remain = redis.call('HGET', currency_key, user)
  if (not remain or tonumber(remain) < math.abs(tonumber(number)))
  then
    return -1
  end
end

return redis.call('HINCRBYFLOAT', currency_key, user, number)
