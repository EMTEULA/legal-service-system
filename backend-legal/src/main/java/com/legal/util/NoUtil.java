package com.legal.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public final class NoUtil {
    private static final AtomicInteger SEQ = new AtomicInteger(100);
    private NoUtil() {}

    public static String next(String prefix) {
        int n = SEQ.updateAndGet(value -> value >= 999 ? 100 : value + 1);
        return prefix + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + n;
    }
}
