package orders_2.logic;

import orders_2.models.Order;
import orders_2.models.PackOrder;
import orders_2.models.UnitOrder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WarehouseManager<T extends Order> {
    public static final int MAX_STOCK = 1_000_000;
    public static final int MAX_RESTOCK = 1_000_000;

    private final List<T> orders = new ArrayList<>();
    private int stock;

    private final Predicate<T> isPending = o -> !o.isFulfilled();
    private final Predicate<T> isUnit = o -> o instanceof UnitOrder;
    private final Predicate<T> fitsStock = o -> o.getRequiredUnits() <= stock;

    private final Comparator<T> byPriorityDesc = Comparator.comparingInt((T o) -> o.getPriority()).reversed();
    private final Comparator<T> byDemandAsc = Comparator.comparingInt((T o) -> o.getRequiredUnits());
    private final Comparator<T> byDemandDesc = byDemandAsc.reversed();
    private final Comparator<T> byIdAsc = Comparator.comparingInt((T o) -> o.getId());

    public WarehouseManager(int initialStock) {
        if (initialStock < 0 || initialStock > MAX_STOCK) {
            throw new IllegalArgumentException("Stock must be between 0 and " + MAX_STOCK);
        }
        this.stock = initialStock;
    }

    public boolean add(T order) {
        if (order == null || order.isFulfilled()) {
            return false;
        }
        if (orders.stream().anyMatch(o -> o.getId() == order.getId())) {
            return false;
        }
        orders.add(order);
        return true;
    }

    public boolean restock(int amount) {
        if (amount < 1 || amount > MAX_RESTOCK) {
            return false;
        }
        if ((long) stock + amount > MAX_STOCK) {
            return false;
        }
        stock += amount;
        return true;
    }

    public int getStock() {
        return stock;
    }

    public Stream<T> getAnalyticsStream() {
        return orders.stream();
    }

    public List<T> process() {
        if (orders.isEmpty() || stock == 0) {
            return new ArrayList<>();
        }
        return process_1();
    }

    private List<T> sortedPending(Comparator<T> comparator) {
        return orders.stream()
                .filter(isPending)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private void fulfill(T order, List<T> done) {
        stock -= order.getRequiredUnits();
        order.markFulfilled();
        done.add(order);
    }

    private long totalDemand(List<T> group) {
        return group.stream().mapToLong(o -> o.getRequiredUnits()).sum();
    }

    private double fulfilledShare(List<T> group) {
        return (double) group.stream().filter(isPending.negate()).count() / group.size();
    }

    private List<T> process_1() {
        List<T> done = new ArrayList<>();
        Map<Integer, Integer> servedCount = new HashMap<>();
        Predicate<T> underLimit = o -> servedCount.getOrDefault(o.getCustomerId(), 0) < 2;
        Comparator<T> order = byPriorityDesc.thenComparing(byDemandAsc).thenComparing(byIdAsc);
        for (T o : sortedPending(order)) {
            if (fitsStock.and(underLimit).test(o)) {
                fulfill(o, done);
                servedCount.merge(o.getCustomerId(), 1, Integer::sum);
            }
        }
        return done;
    }

    private List<T> process_2() {
        List<T> done = new ArrayList<>();
        int reserve = (stock + 3) / 4;
        Predicate<T> keepsReserve = o -> stock - o.getRequiredUnits() >= reserve;
        Comparator<T> order = byPriorityDesc.thenComparing(byDemandDesc).thenComparing(byIdAsc);
        for (T o : sortedPending(order)) {
            if (keepsReserve.test(o)) {
                fulfill(o, done);
            }
        }
        return done;
    }

    private List<T> process_3() {
        List<T> done = new ArrayList<>();
        Set<Integer> servedCustomers = new HashSet<>();
        Predicate<T> notServed = o -> !servedCustomers.contains(o.getCustomerId());
        Comparator<T> order = byPriorityDesc.thenComparing(byIdAsc);
        for (T o : sortedPending(order)) {
            if (fitsStock.and(notServed).test(o)) {
                fulfill(o, done);
                servedCustomers.add(o.getCustomerId());
            }
        }
        return done;
    }

    private List<T> process_4() {
        List<T> done = new ArrayList<>();
        Map<Integer, List<T>> groups = orders.stream()
                .filter(isPending)
                .sorted(byIdAsc)
                .collect(Collectors.groupingBy(o -> o.getCustomerId(), LinkedHashMap::new, Collectors.toList()));
        Comparator<Map.Entry<Integer, List<T>>> groupOrder = Comparator
                .comparingLong((Map.Entry<Integer, List<T>> e) -> totalDemand(e.getValue()))
                .thenComparingInt(e -> e.getKey());
        Predicate<List<T>> groupFits = group -> totalDemand(group) <= stock;
        List<Map.Entry<Integer, List<T>>> sortedGroups = groups.entrySet().stream()
                .sorted(groupOrder)
                .collect(Collectors.toList());
        for (Map.Entry<Integer, List<T>> entry : sortedGroups) {
            if (groupFits.test(entry.getValue())) {
                for (T o : entry.getValue()) {
                    fulfill(o, done);
                }
            }
        }
        return done;
    }

    private List<T> process_5() {
        List<T> done = new ArrayList<>();
        Comparator<T> order = byPriorityDesc.thenComparing(byIdAsc);
        List<T> units = orders.stream()
                .filter(isPending.and(isUnit))
                .sorted(order)
                .collect(Collectors.toList());
        List<T> packs = orders.stream()
                .filter(isPending.and(isUnit.negate()))
                .sorted(order)
                .collect(Collectors.toList());
        List<T> merged = new ArrayList<>();
        for (int i = 0; i < Math.max(units.size(), packs.size()); i++) {
            if (i < units.size()) {
                merged.add(units.get(i));
            }
            if (i < packs.size()) {
                merged.add(packs.get(i));
            }
        }
        for (T o : merged) {
            if (fitsStock.test(o)) {
                fulfill(o, done);
            }
        }
        return done;
    }

    private List<T> process_6() {
        List<T> done = new ArrayList<>();
        Map<Integer, Long> previousFulfilled = orders.stream()
                .filter(isPending.negate())
                .collect(Collectors.groupingBy(o -> o.getCustomerId(), Collectors.counting()));
        Comparator<T> order = Comparator
                .comparingLong((T o) -> previousFulfilled.getOrDefault(o.getCustomerId(), 0L))
                .thenComparing(byPriorityDesc)
                .thenComparing(byIdAsc);
        for (T o : sortedPending(order)) {
            if (fitsStock.test(o)) {
                fulfill(o, done);
            }
        }
        return done;
    }

    public long analytics_1() {
        long totalPending = orders.stream()
                .filter(isPending)
                .mapToLong(o -> o.getRequiredUnits())
                .sum();
        return Math.max(0L, totalPending - stock);
    }

    public Map<Integer, Long> analytics_2() {
        return orders.stream()
                .filter(isPending)
                .collect(Collectors.groupingBy(o -> o.getCustomerId(), TreeMap::new,
                        Collectors.summingLong(o -> o.getRequiredUnits())));
    }

    public Optional<T> analytics_3() {
        return orders.stream()
                .filter(isPending.and(fitsStock))
                .min(byPriorityDesc.thenComparing(byDemandDesc).thenComparing(byIdAsc));
    }

    public List<Integer> analytics_4() {
        return orders.stream()
                .map(o -> o.getCustomerId())
                .distinct()
                .filter(c -> orders.stream().anyMatch(o -> o.getCustomerId() == c && o.isFulfilled()))
                .filter(c -> orders.stream().anyMatch(o -> o.getCustomerId() == c && !o.isFulfilled()))
                .sorted()
                .collect(Collectors.toList());
    }

    public Map<String, Long> analytics_5() {
        return Stream.of(UnitOrder.class.getSimpleName(), PackOrder.class.getSimpleName())
                .collect(Collectors.toMap(
                        Function.identity(),
                        name -> orders.stream()
                                .filter(o -> o.isFulfilled() && o.getClass().getSimpleName().equals(name))
                                .mapToLong(o -> o.getRequiredUnits())
                                .sum(),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    public Optional<Integer> analytics_6() {
        Comparator<Map.Entry<Integer, List<T>>> byShareDescThenId = Comparator
                .comparingDouble((Map.Entry<Integer, List<T>> e) -> fulfilledShare(e.getValue()))
                .reversed()
                .thenComparingInt(e -> e.getKey());
        return orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCustomerId()))
                .entrySet().stream()
                .filter(e -> e.getValue().size() >= 2)
                .min(byShareDescThenId)
                .map(Map.Entry::getKey);
    }
}
