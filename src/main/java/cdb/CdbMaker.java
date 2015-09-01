package cdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CdbMaker implements AutoCloseable {

    private CdbFile file;

    private List<List<DatumDescriptor>> datumDescriptorsList;
    private int endOfDataOffset;

    public CdbMaker(String fileName) throws IOException {
        endOfDataOffset = Header.BYTES_SIZE;
        file = new CdbFile(fileName, CdbFileMode.Write);
        file.seek(endOfDataOffset);
        datumDescriptorsList = new ArrayList<>(Header.ENTRY_COUNT);
        for (int i = 0; i < Header.ENTRY_COUNT; i++) {
            datumDescriptorsList.add(new ArrayList<>());
        }
    }

    @Override
    public void close() throws IOException {
        writeSlots();
        writeHeader();
        file.close();
    }

    private void writeHeader() throws IOException {
        file.seek(0);
        int slotsOffset = endOfDataOffset;
        List<HeaderEntry> entries = new ArrayList<>(Header.ENTRY_COUNT);
        for (List<DatumDescriptor> datumDescriptors : datumDescriptorsList) {
            HeaderEntry entry = new HeaderEntry(slotsOffset, slotCountOf(datumDescriptors));
            entries.add(entry);
            slotsOffset += entry.getSlotsCount() * Slot.BYTES_SIZE;
        }
        file.writeHeader(new Header(entries));
    }

    private void writeSlots() throws IOException {
        for (List<DatumDescriptor> datumDescriptors : datumDescriptorsList) {
            int slotCount = slotCountOf(datumDescriptors);
            Slot[] slots = new Slot[slotCount];
            for (DatumDescriptor datumDescriptor : datumDescriptors) {
                int i = (datumDescriptor.hash >>> 8) % slotCount;
                while (slots[i] != null) {
                    i = (i + 1) % slotCount;
                }
                slots[i] = new Slot(datumDescriptor.hash, datumDescriptor.offset);
            }
            for (Slot slot : slots) {
                file.writeSlot(slot != null ? slot : Slot.EMPTY_SLOT);
            }
        }
    }

    private int slotCountOf(List<DatumDescriptor> datumDescriptors) {
        return datumDescriptors.size() * 2;
    }

    public void put(byte[] key, byte[] value) throws IOException {
        int keyHash = CdbHash.calculate(key);
        datumDescriptorsList.get(CdbHash.modulo(keyHash, Header.ENTRY_COUNT)).add(
                new DatumDescriptor(keyHash, endOfDataOffset));
        Datum datum = new Datum(key, value);

        file.seek(endOfDataOffset);
        endOfDataOffset += file.writeDatum(datum);
    }

    private static class DatumDescriptor {
        final int hash;
        final int offset;

        private DatumDescriptor(int hash, int offset) {
            this.hash = hash;
            this.offset = offset;
        }
    }
}
