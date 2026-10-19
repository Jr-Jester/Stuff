package models;

public class UnitOrder extends Order {
    private final int quantity;

    public UnitOrder(int id, int customerId, int priority, int quantity) {
        super(id, customerId, priority);
        if (quantity < 1 || quantity > 1000) {
            throw new IllegalArgumentException("Quantity must be between 1 and 1000");
        }
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public int getRequiredUnits() {
        return quantity;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + String.format(" | quantity %d", quantity);
    }
}
