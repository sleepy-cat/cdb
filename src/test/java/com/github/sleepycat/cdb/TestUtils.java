package com.github.sleepycat.cdb;

import java.util.Random;

public class TestUtils {

    private static final Random random = new Random();

    public static byte[] randomBytes(int len) {
        byte[] result = new byte[len];
        random.nextBytes(result);
        return result;
    }
}
