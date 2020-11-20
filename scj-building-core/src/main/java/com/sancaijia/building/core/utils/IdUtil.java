package com.sancaijia.building.core.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class IdUtil {
    private static final char[] DIGITS66 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '-', '.', '_', '~'};

    public IdUtil() {
    }

    private static String toIDString(long i) {
        char[] buf = new char[32];
        int z = 64;
        int cp = 32;
        long b = (long)(z - 1);

        do {
            --cp;
            buf[cp] = DIGITS66[(int)(i & b)];
            i >>>= 6;
        } while(i != 0L);

        return new String(buf, cp, 32 - cp);
    }

    public static Long nextLongId() {
        return (new IdWorker()).nextId();
    }

    public static String nextStringId() {
        UUID u = UUID.randomUUID();
        String id = toIDString(u.getMostSignificantBits()) + toIDString(u.getLeastSignificantBits());
        return DigestUtils.shaHex(id);
    }
}
