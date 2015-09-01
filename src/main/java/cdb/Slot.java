package cdb;

public class Slot {

    public static final Slot EMPTY_SLOT = new Slot(0, 0);

    public static final int BYTES_SIZE = Integer.BYTES /* sizeOf(hash) */ + Integer.BYTES /* sizeOf(datumOffset) */;

    private final int hash;
    private final int datumOffset;

    public Slot(int hash, int datumOffset) {
        this.hash = hash;
        this.datumOffset = datumOffset;
    }

    public int getHash() {
        return hash;
    }

    public int getDatumOffset() {
        return datumOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Slot slot = (Slot) o;
        return hash == slot.hash && datumOffset == slot.datumOffset;
    }

    @Override
    public int hashCode() {
        int result = hash;
        result = 31 * result + datumOffset;
        return result;
    }
}
