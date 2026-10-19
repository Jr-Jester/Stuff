package orders_2.models;

public class PackOrder extends Order {
    private final int packCount;
    private final int packSize;

    public PackOrder(int id, int customerId, int priority, int packCount, int packSize) {
        super(id, customerId, priority);
        if (packCount < 1 || packCount > 1000) {
            throw new IllegalArgumentException("Pack count must be between 1 and 1000");
        }
        if (packSize < 1 || packSize > 1000) {
            throw new IllegalArgumentException("Pack size must be between 1 and 1000");
        }
        this.packCount = packCount;
        this.packSize = packSize;
    }

    public int getPackCount() {
        return packCount;
    }

    public int getPackSize() {
        return packSize;
    }

    @Override
    public int getRequiredUnits() {
        return packCount * packSize;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + String.format(" | packs %d x %d", packCount, packSize);
    }
}
