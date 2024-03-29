## 金额存放在Redis的方案

## 背景

如果将金额存放在关系型数据库，行级锁处理金额在高并发的情况下响应时间是无法接受的，例如：

直播间送礼操作: 

在关系型数据库中我们需要查询送礼者的余额，再更新发送者和 n 个接受者的余额，还需要新增 n + 1 条明细(可最终一致)。

流水明细异步保存的情况下需要读 1 次(发送者的余额)，写  n + 1 (减发送者，加接受者余额) 次。整个操作被事务包裹，响应很慢。

## 解决方案

将金额存放在 Redis 中，无论是查询、增减都会有一个高效的响应时间，使用 Lua 脚本保证原子性，流水明细通过消息队列保证最终一致性，此时响应是非常快的，因为我们的操作只有一条 Lua 脚本(Lua 脚本逻辑不宜过于复杂)。

代码实现: [Github](https://github.com/no-today/records/tree/master/amount-redis)
