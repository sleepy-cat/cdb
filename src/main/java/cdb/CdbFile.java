package cdb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class CdbFile {

    private final RandomAccessFile raf;

    public CdbFile(String fileName, CdbFileMode mode) throws FileNotFoundException {
        raf = new RandomAccessFile(fileName, mode == CdbFileMode.Write ? "rw" : "r");
    }

    public void close() throws IOException {
        raf.close();
    }

    public void seek(long pos) throws IOException {
        raf.seek(pos);
    }

    public long size() throws IOException {
        return raf.length();
    }

    public Header readHeader() throws IOException {
        List<HeaderEntry> entries = new ArrayList<>(Header.ENTRY_COUNT);
        for (int i = 0; i < Header.ENTRY_COUNT; i++) {
            entries.add(readHeaderEntry());
        }
        return new Header(entries);
    }

    public int writeHeader(Header value) throws IOException {
        int result = 0;
        for (HeaderEntry entry : value) {
            result += writeHeaderEntry(entry);
        }
        return result;
    }

    public Datum readDatum() throws IOException {
        int keyLen = readInt();
        int valueLen = readInt();
        return new Datum(readBytes(keyLen), readBytes(valueLen));
    }

    public int writeDatum(Datum value) throws IOException {
        int result = 0;
        result += writeInt(value.getKey().length);
        result += writeInt(value.getValue().length);
        result += writeBytes(value.getKey());
        result += writeBytes(value.getValue());
        return result;
    }

    public Slot readSlot() throws IOException {
        return new Slot(readInt(), readInt());
    }

    public int writeSlot(Slot value) throws IOException {
        int result = 0;
        result += writeInt(value.getHash());
        result += writeInt(value.getDatumOffset());
        return result;
    }

    private HeaderEntry readHeaderEntry() throws IOException {
        return new HeaderEntry(readInt(), readInt());
    }

    private int writeHeaderEntry(HeaderEntry value) throws IOException {
        int result = 0;
        result += writeInt(value.getSlotsOffset());
        result += writeInt(value.getSlotsCount());
        return result;
    }

    private int readInt() throws IOException {
        byte[] buffer = new byte[4];
        raf.readFully(buffer);
        return ((buffer[0] & 0xFF) << 24) +
                ((buffer[1] & 0xFF) << 16) +
                ((buffer[2] & 0xFF) << 8) +
                (buffer[3] & 0xFF);
    }

    private int writeInt(int value) throws IOException {
        byte[] buffer = new byte[4];
        buffer[0] = (byte) (value >> 24);
        buffer[1] = (byte) (value >> 16);
        buffer[2] = (byte) (value >> 8);
        buffer[3] = (byte) value;
        return writeBytes(buffer);
    }

    private byte[] readBytes(int len) throws IOException {
        byte[] result = new byte[len];
        raf.readFully(result);
        return result;
    }

    private int writeBytes(byte[] b) throws IOException {
        raf.write(b);
        return b.length;
    }
}
