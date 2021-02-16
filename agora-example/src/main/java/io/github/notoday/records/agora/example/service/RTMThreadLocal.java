package io.github.notoday.records.agora.example.service;

/**
 * RTM 存储线程数据
 * 一层层将参数带进去改动太大了, 利用 ThreadLocal 跨方法取值
 */
public class RTMThreadLocal {

    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    public static void setTimestamp(long timestamp) {
        THREAD_LOCAL.set(timestamp);
    }

    public static Long getTimestamp() {
        Long timestamp = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();
        return timestamp;
    }
}
