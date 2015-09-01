package cdb;

public class KeyCursor {

    private int headerEntryIndex;
    private int headerEntrySlotIndex;

    int getHeaderEntryIndex() {
        return headerEntryIndex;
    }

    void setHeaderEntryIndex(int headerEntryIndex) {
        this.headerEntryIndex = headerEntryIndex;
    }

    int getHeaderEntrySlotIndex() {
        return headerEntrySlotIndex;
    }

    void setHeaderEntrySlotIndex(int headerEntrySlotIndex) {
        this.headerEntrySlotIndex = headerEntrySlotIndex;
    }
}
