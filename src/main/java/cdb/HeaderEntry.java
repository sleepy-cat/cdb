package cdb;

class HeaderEntry {

    public static final int BYTES_SIZE = Integer.BYTES /* sizeOf(slotsOffset) */ + Integer.BYTES /* sizeOf(slotsCount) */;

    final int slotsOffset;
    final int slotsCount;

    HeaderEntry(int slotsOffset, int slotsCount) {
        this.slotsOffset = slotsOffset;
        this.slotsCount = slotsCount;
    }

    public int getSlotsOffset() {
        return slotsOffset;
    }

    public int getSlotsCount() {
        return slotsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HeaderEntry that = (HeaderEntry) o;
        return slotsCount == that.slotsCount && slotsOffset == that.slotsOffset;

    }

    @Override
    public int hashCode() {
        int result = slotsOffset;
        result = 31 * result + slotsCount;
        return result;
    }
}
