package io.github.notoday.records.atomic.redis.service;

/**
 * @author no-today
 * @date 2021/02/08 下午5:14
 */
public interface LuaScript {

    // File path is resource/lua/incr_decr.lua
    String SCRIPT_INCR_DECR =
            "local currency_key      = KEYS[1]\n" +
            "\n" +
            "local user              = ARGV[1]\n" +
            "local number            = ARGV[2]\n" +
            "\n" +
            "if (tonumber(number) < 0)\n" +
            "then\n" +
            "  local remain = redis.call('HGET', currency_key, user)\n" +
            "  if (not remain or tonumber(remain) < math.abs(tonumber(number)))\n" +
            "  then\n" +
            "    return -1\n" +
            "  end\n" +
            "end\n" +
            "\n" +
            "return redis.call('HINCRBYFLOAT', currency_key, user, number)";

    // File path is resource/lua/send_gift.lua
    String SCRIPT_SEND_GIFT =
            "local gold_coin_key = KEYS[1]\n" +
            "local gold_bean_key = KEYS[2]\n" +
            "\n" +
            "local sender        = KEYS[3]\n" +
            "local sender_decr   = KEYS[4]\n" +
            "local receive_incr  = KEYS[5]\n" +
            "\n" +
            "if (tonumber(sender_decr) ~= 0)\n" +
            "then\n" +
            "  local remain = redis.call('HGET', gold_coin_key, sender)\n" +
            "  if (not remain or tonumber(remain) < sender_decr)\n" +
            "  then\n" +
            "    return -1\n" +
            "  end\n" +
            "end\n" +
            "\n" +
            "if (tonumber(sender_decr) ~= 0)\n" +
            "then\n" +
            "  redis.call('HINCRBY', gold_coin_key, sender, sender_decr)\n" +
            "end\n" +
            "\n" +
            "for i = 1, #ARGV do\n" +
            "  redis.call('HINCRBY', gold_bean_key, ARGV[i], receive_incr)\n" +
            "end\n" +
            "\n" +
            "return 1";

    // File path is resource/lua/exchange.lua
    String SCRIPT_EXCHANGE =
            "local gold_coin_key = KEYS[1]\n" +
            "local gold_bean_key = KEYS[2]\n" +
            "\n" +
            "local user          = ARGV[1]\n" +
            "local deduction     = ARGV[2]\n" +
            "local increase      = ARGV[3]\n" +
            "\n" +
            "local remain = redis.call('HGET', gold_bean_key, user)\n" +
            "if (not remain or tonumber(remain) < tonumber(deduction))\n" +
            "then\n" +
            "  return -1\n" +
            "end\n" +
            "\n" +
            "redis.call('HINCRBY', gold_bean_key, user, deduction)\n" +
            "redis.call('HINCRBY', gold_coin_key, user, increase)\n" +
            "\n" +
            "return 1";
}
