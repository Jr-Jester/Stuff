package main;

import logic.WarehouseManager;
import models.Order;
import models.PackOrder;
import models.UnitOrder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        WarehouseManager<Order> manager = createInitialManager();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        do {
            printMenu();
            try {
                int choice = readInt(scanner, "Choose an option: ");
                switch (choice) {
                    case 1:
                        addOrder(scanner, manager);
                        break;
                    case 2:
                        showOrders(manager);
                        break;
                    case 3:
                        restock(scanner, manager);
                        break;
                    case 4:
                        processOrders(manager);
                        break;
                    case 5:
                        System.out.println("Analytics result: " + manager.analytics_1());
                        break;
                    case 6:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Error: unknown menu item, choose 1-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a whole number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NoSuchElementException e) {
                running = false;
            }
        } while (running);
    }

    private static WarehouseManager<Order> createInitialManager() {
        WarehouseManager<Order> manager = new WarehouseManager<>(15);
        manager.add(new UnitOrder(1, 1, 5, 9));
        manager.add(new UnitOrder(4, 2, 2, 3));
        manager.add(new UnitOrder(5, 3, 4, 5));
        manager.add(new UnitOrder(8, 5, 1, 1));
        manager.add(new UnitOrder(9, 2, 1, 2));
        manager.add(new UnitOrder(11, 1, 1, 2));
        manager.add(new PackOrder(2, 1, 4, 2, 3));
        manager.add(new PackOrder(3, 2, 5, 2, 2));
        manager.add(new PackOrder(6, 4, 3, 2, 5));
        manager.add(new PackOrder(7, 3, 4, 1, 2));
        manager.add(new PackOrder(10, 2, 1, 1, 3));
        return manager;
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Warehouse =====");
        System.out.println("1. Add order");
        System.out.println("2. Show all orders and stock");
        System.out.println("3. Restock");
        System.out.println("4. Process orders");
        System.out.println("5. Analytics");
        System.out.println("6. Exit");
    }

    private static int readInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private static void addOrder(Scanner scanner, WarehouseManager<Order> manager) {
        int type = readInt(scanner, "Order type (1 - UnitOrder, 2 - PackOrder): ");
        if (type != 1 && type != 2) {
            System.out.println("Error: unknown order type.");
            return;
        }
        int id = readInt(scanner, "Order id: ");
        int customerId = readInt(scanner, "Customer id: ");
        int priority = readInt(scanner, "Priority (1-5): ");
        Order order;
        if (type == 1) {
            int quantity = readInt(scanner, "Quantity (1-1000): ");
            order = new UnitOrder(id, customerId, priority, quantity);
        } else {
            int packCount = readInt(scanner, "Pack count (1-1000): ");
            int packSize = readInt(scanner, "Pack size (1-1000): ");
            order = new PackOrder(id, customerId, priority, packCount, packSize);
        }
        if (manager.add(order)) {
            System.out.println("Order added: " + order.getDetails());
        } else {
            System.out.println("Error: order with id " + id + " already exists.");
        }
    }

    private static void showOrders(WarehouseManager<Order> manager) {
        if (manager.getAnalyticsStream().count() == 0) {
            System.out.println("No orders.");
        } else {
            manager.getAnalyticsStream().map(Order::getDetails).forEach(System.out::println);
        }
        System.out.println("Current stock: " + manager.getStock());
    }

    private static void restock(Scanner scanner, WarehouseManager<Order> manager) {
        int amount = readInt(scanner, "Amount (1-" + WarehouseManager.MAX_RESTOCK + "): ");
        if (manager.restock(amount)) {
            System.out.println("Stock replenished. Current stock: " + manager.getStock());
        } else {
            System.out.println("Error: amount must be between 1 and " + WarehouseManager.MAX_RESTOCK
                    + " and stock cannot exceed " + WarehouseManager.MAX_STOCK + ".");
        }
    }

    private static void processOrders(WarehouseManager<Order> manager) {
        List<Order> done = manager.process();
        if (done.isEmpty()) {
            System.out.println("No orders were fulfilled.");
        } else {
            System.out.println("Fulfilled in this run:");
            done.forEach(o -> System.out.println(o.getDetails()));
        }
        System.out.println("Current stock: " + manager.getStock());
    }
}
