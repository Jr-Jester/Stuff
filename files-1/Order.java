package models;

import interfaces.StockDemand;

public abstract class Order implements StockDemand {
    private final int id;
    private final int customerId;
    private final int priority;
    private boolean fulfilled;

    protected Order(int id, int customerId, int priority) {
        if (id <= 0) {
            throw new IllegalArgumentException("Order id must be a positive number");
        }
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer id must be a positive number");
        }
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }
        this.id = id;
        this.customerId = customerId;
        this.priority = priority;
        this.fulfilled = false;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    public void markFulfilled() {
        fulfilled = true;
    }

    public String getDetails() {
        return String.format("%-9s | id %3d | customer %3d | priority %d | demand %7d | %-9s",
                getClass().getSimpleName(), id, customerId, priority, getRequiredUnits(),
                fulfilled ? "fulfilled" : "pending");
    }

    @Override
    public String toString() {
        return getDetails();
    }
}
