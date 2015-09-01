package com.github.sleepycat.cdb;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class CdbMakerTest {

    private static final String FILE_NAME = "test.cdb";

    @AfterMethod
    public void tearDown() throws Exception {
        Files.delete(Paths.get(FILE_NAME));
    }

    @Test
    public void testPut() throws Exception {
        byte[] key = new byte[]{(byte) 0xFF, (byte) 0xBB, (byte) 0xCC, (byte) 0x1, (byte) 0x2, (byte) 0x3};
        byte[] value = new byte[]{(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0x1, (byte) 0x2, (byte) 0x3};
        try (CdbMaker writer = new CdbMaker(FILE_NAME)) {
            writer.put(key, value);
        }
        // Check if header was written correctly
        Assert.assertTrue(Files.size(Paths.get(FILE_NAME)) > Header.BYTES_SIZE);
    }

    @Test
    public void testMassivePut() throws Exception {
        try (CdbMaker writer = new CdbMaker(FILE_NAME)) {
            for (int n = 0; n < 1000; n++) {
                writer.put(TestUtils.randomBytes(2048), TestUtils.randomBytes(4 * 2048));
            }
        }
        // Check if header was written correctly
        Assert.assertTrue(Files.size(Paths.get(FILE_NAME)) > Header.BYTES_SIZE);
    }
}
