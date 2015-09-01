package com.github.sleepycat.cdb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Header implements Iterable<HeaderEntry> {

    public static final int ENTRY_COUNT = 256;
    public static final int BYTES_SIZE = HeaderEntry.BYTES_SIZE * ENTRY_COUNT;

    private final List<HeaderEntry> entries;

    public Header(List<HeaderEntry> entries) {
        if (entries == null || entries.size() != ENTRY_COUNT) {
            throw new IllegalArgumentException("entries");
        }
        this.entries = new ArrayList<>(entries);
    }

    public HeaderEntry get(int index) {
        return entries.get(index);
    }

    public int size() {
        return entries.size();
    }

    @Override
    public Iterator<HeaderEntry> iterator() {
        return entries.iterator();
    }
}
