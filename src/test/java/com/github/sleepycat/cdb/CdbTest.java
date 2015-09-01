package com.github.sleepycat.cdb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class CdbTest {

    private static final String FILE_NAME = "test.cdb";

    @AfterMethod
    public void tearDown() throws Exception {
        Files.delete(Paths.get(FILE_NAME));
    }

    @Test
    public void testGet() throws Exception {
        byte[] key = new byte[]{(byte) 0xFF, (byte) 0xBB, (byte) 0xCC, (byte) 0x1, (byte) 0x2, (byte) 0x3};
        byte[] value = new byte[]{(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0x1, (byte) 0x2, (byte) 0x3};
        try (CdbMaker writer = new CdbMaker(FILE_NAME)) {
            writer.put(key, value);
        }
        try (Cdb cdb = new Cdb(FILE_NAME)) {
            Optional<byte[]> v = cdb.get(key);
            Assert.assertTrue(v.isPresent());
            Assert.assertEquals(v.get(), value);
        }
    }

    @Test
    public void testMassiveGet() throws Exception {
        Map<ByteArrayWrapper, byte[]> map = massiveSeed();
        try (Cdb cdb = new Cdb(FILE_NAME)) {
            for (Map.Entry<ByteArrayWrapper, byte[]> entry : map.entrySet()) {
                Optional<byte[]> v = cdb.get(entry.getKey().get());
                Assert.assertTrue(v.isPresent());
                Assert.assertEquals(v.get(), entry.getValue());
            }
        }
    }

    @Test
    public void testGetAbsent() throws Exception {
        Map<ByteArrayWrapper, byte[]> map = massiveSeed();
        try (Cdb cdb = new Cdb(FILE_NAME)) {
            byte[] key = TestUtils.randomBytes(512);
            Optional<byte[]> v = cdb.get(key);
            Assert.assertFalse(map.containsKey(key));
            Assert.assertFalse(v.isPresent());
        }
    }

    @Test
    public void testGetNextKey() throws Exception {
        Map<ByteArrayWrapper, byte[]> map = massiveSeed();
        int keyCount = 0;
        try (Cdb cdb = new Cdb(FILE_NAME)) {
            KeyCursor cursor = new KeyCursor();
            Optional<byte[]> optionalKey;
            while ((optionalKey = cdb.getNextKey(cursor)).isPresent()) {
                byte[] b = optionalKey.get();
                Assert.assertTrue(map.containsKey(new ByteArrayWrapper(b)));
                keyCount++;
            }
        }
        Assert.assertEquals(keyCount, map.size());
    }

    private Map<ByteArrayWrapper, byte[]> massiveSeed() throws IOException {
        Map<ByteArrayWrapper, byte[]> result = new HashMap<>(100_000);
        try (CdbMaker maker = new CdbMaker(FILE_NAME)) {
            for (int n = 0; n < 1000; n++) {
                byte[] key = TestUtils.randomBytes(2048);
                byte[] value = TestUtils.randomBytes(4 * 2048);
                result.put(new ByteArrayWrapper(key), value);
                maker.put(key, value);
            }
        }
        return result;
    }

    private static final class ByteArrayWrapper {

        private final byte[] array;

        private ByteArrayWrapper(byte[] array) {
            this.array = array;
        }

        public byte[] get() {
            return array;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ByteArrayWrapper wrapper = (ByteArrayWrapper) o;
            return Arrays.equals(array, wrapper.array);
        }

        @Override
        public int hashCode() {
            return array != null ? Arrays.hashCode(array) : 0;
        }
    }
}
