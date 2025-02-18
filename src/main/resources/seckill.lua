---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by Wolleaf Lee.
--- DateTime: 2024/11/24 12:51
---

-- 优惠券id
local voucherId = ARGV[1]
-- 优惠券库存 key
local voucherStockKey = 'seckill:stock:' .. voucherId
-- 用户集合key
local userSetKey = 'seckill:order:' .. voucherId
-- 用户id
local userId = ARGV[2]
-- 订单id
local orderId = ARGV[3]
-- 判断库存是否充足
if (tonumber(redis.call('get', voucherStockKey)) <= 0) then
    return 1
end
-- 判断用户是否已经领取过优惠券
if (redis.call('sismember', userSetKey, userId) == 1) then
    return 2
end
-- 库存充足，并且用户没有领取过优惠券，则扣减库存，并且添加到已领取优惠券集合中
redis.call('incrby', voucherStockKey, -1)
redis.call('sadd', userSetKey, userId)
redis.call('xadd', 'stream.orders', '*', 'userId', userId, 'voucherId', voucherId, 'id', orderId)
return 0