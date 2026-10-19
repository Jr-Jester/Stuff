package logic;

import models.AcademicApplication;
import models.ResearchApplication;
import models.ScholarshipApplication;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScholarshipManager<T extends ScholarshipApplication> {
    private final List<T> applications = new ArrayList<>();

    private final Predicate<T> isAcademic = a -> a instanceof AcademicApplication;
    private final Predicate<T> isResearch = a -> a instanceof ResearchApplication;
    private final Comparator<T> byScoreDescThenId = Comparator
            .comparingDouble((T a) -> a.calculateScore())
            .reversed()
            .thenComparingInt(a -> a.getId());

    public boolean add(T application) {
        if (application == null) {
            return false;
        }
        if (applications.stream().anyMatch(a -> a.getId() == application.getId())) {
            return false;
        }
        applications.add(application);
        applications.forEach(a -> a.setAwarded(false));
        return true;
    }

    public List<T> allocate(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit cannot be negative");
        }
        Map<Integer, List<T>> byStudent = groupByStudent();
        Map<Integer, Double> ratings = new HashMap<>();
        for (Map.Entry<Integer, List<T>> entry : byStudent.entrySet()) {
            OptionalDouble rating = studentRating(entry.getValue());
            if (rating.isPresent()) {
                ratings.put(entry.getKey(), rating.getAsDouble());
            }
        }
        Comparator<Integer> byRatingDescThenId = Comparator
                .comparingDouble((Integer s) -> ratings.get(s))
                .reversed()
                .thenComparingInt(s -> s);
        List<Integer> winners = ratings.keySet().stream()
                .sorted(byRatingDescThenId)
                .limit(limit)
                .collect(Collectors.toList());
        applications.forEach(a -> a.setAwarded(false));
        List<T> result = new ArrayList<>();
        for (Integer studentId : winners) {
            T best = byStudent.get(studentId).stream().min(byScoreDescThenId).get();
            best.setAwarded(true);
            result.add(best);
        }
        return result;
    }

    public OptionalDouble getStudentRating(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student id must be a positive number");
        }
        List<T> studentApps = applications.stream()
                .filter(a -> a.getStudentId() == studentId)
                .collect(Collectors.toList());
        if (studentApps.isEmpty()) {
            return OptionalDouble.empty();
        }
        return studentRating(studentApps);
    }

    public Stream<T> getAnalyticsStream() {
        return applications.stream();
    }

    private OptionalDouble studentRating(List<T> apps) {
        return rating_1(apps);
    }

    private Map<Integer, List<T>> groupByStudent() {
        return applications.stream()
                .collect(Collectors.groupingBy(a -> a.getStudentId(), TreeMap::new, Collectors.toList()));
    }

    private Predicate<T> scoreAtLeast(double min) {
        return a -> a.calculateScore() >= min;
    }

    private OptionalDouble bestOfType(List<T> apps, Predicate<T> type) {
        return apps.stream().filter(type).mapToDouble(a -> a.calculateScore()).max();
    }

    private Predicate<List<T>> hasBothTypesAtLeast60() {
        return apps -> apps.stream().anyMatch(isAcademic.and(scoreAtLeast(60)))
                && apps.stream().anyMatch(isResearch.and(scoreAtLeast(60)));
    }

    private OptionalDouble rating_1(List<T> apps) {
        Predicate<List<T>> admitted = list -> list.size() >= 2
                && list.stream().allMatch(scoreAtLeast(40))
                && list.stream().anyMatch(scoreAtLeast(60));
        if (!admitted.test(apps)) {
            return OptionalDouble.empty();
        }
        return apps.stream().mapToDouble(a -> a.calculateScore()).max();
    }

    private OptionalDouble rating_2(List<T> apps) {
        Predicate<List<T>> admitted = list -> list.size() >= 2
                && list.stream().allMatch(scoreAtLeast(60));
        if (!admitted.test(apps)) {
            return OptionalDouble.empty();
        }
        return apps.stream().mapToDouble(a -> a.calculateScore()).average();
    }

    private OptionalDouble rating_3(List<T> apps) {
        if (!hasBothTypesAtLeast60().test(apps)) {
            return OptionalDouble.empty();
        }
        double bestAcademic = bestOfType(apps, isAcademic).getAsDouble();
        double bestResearch = bestOfType(apps, isResearch).getAsDouble();
        return OptionalDouble.of((bestAcademic + bestResearch) / 2.0);
    }

    private OptionalDouble rating_4(List<T> apps) {
        if (!hasBothTypesAtLeast60().test(apps)) {
            return OptionalDouble.empty();
        }
        double bestAcademic = bestOfType(apps, isAcademic).getAsDouble();
        double bestResearch = bestOfType(apps, isResearch).getAsDouble();
        return OptionalDouble.of(Math.min(bestAcademic, bestResearch));
    }

    private OptionalDouble rating_5(List<T> apps) {
        Predicate<List<T>> admitted = list -> list.stream().filter(scoreAtLeast(80)).count() >= 2;
        if (!admitted.test(apps)) {
            return OptionalDouble.empty();
        }
        return apps.stream()
                .map(a -> a.calculateScore())
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .mapToDouble(d -> d)
                .average();
    }

    private OptionalDouble rating_6(List<T> apps) {
        Predicate<List<T>> admitted = list -> list.size() >= 2
                && list.stream().allMatch(scoreAtLeast(60))
                && list.stream().mapToDouble(a -> a.calculateScore()).max().getAsDouble()
                - list.stream().mapToDouble(a -> a.calculateScore()).min().getAsDouble() <= 20;
        if (!admitted.test(apps)) {
            return OptionalDouble.empty();
        }
        return apps.stream().mapToDouble(a -> a.calculateScore()).min();
    }

    public List<Integer> analytics_1() {
        Comparator<Map.Entry<Integer, Double>> byMaxDescThenId = Map.Entry.<Integer, Double>comparingByValue()
                .reversed()
                .thenComparing(Map.Entry.comparingByKey());
        return applications.stream()
                .collect(Collectors.toMap(a -> a.getStudentId(), a -> a.calculateScore(), Math::max))
                .entrySet().stream()
                .filter(e -> e.getValue() >= 60)
                .sorted(byMaxDescThenId)
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Integer> analytics_2() {
        return groupByStudent().entrySet().stream()
                .filter(e -> {
                    OptionalDouble academic = bestOfType(e.getValue(), isAcademic);
                    OptionalDouble research = bestOfType(e.getValue(), isResearch);
                    return academic.isPresent() && research.isPresent()
                            && Math.abs(academic.getAsDouble() - research.getAsDouble()) <= 10;
                })
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    public Map<Integer, Double> analytics_3() {
        return groupByStudent().entrySet().stream()
                .filter(e -> e.getValue().size() >= 2)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().mapToDouble(a -> a.calculateScore()).average().getAsDouble(),
                        (a, b) -> a,
                        TreeMap::new));
    }

    public List<Integer> analytics_4() {
        return groupByStudent().entrySet().stream()
                .filter(e -> e.getValue().size() >= 2)
                .filter(e -> e.getValue().stream().allMatch(scoreAtLeast(60)))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<ScholarshipApplication> analytics_5() {
        Map<String, Double> averageByType = applications.stream()
                .collect(Collectors.groupingBy(a -> a.getClass().getSimpleName(),
                        Collectors.averagingDouble(a -> a.calculateScore())));
        Comparator<T> byDeviationThenId = Comparator
                .comparingDouble((T a) -> Math.abs(a.calculateScore()
                        - averageByType.get(a.getClass().getSimpleName())))
                .thenComparingInt(a -> a.getId());
        return applications.stream()
                .min(byDeviationThenId)
                .map(a -> a);
    }

    public Map<String, Long> analytics_6() {
        return Stream.of(AcademicApplication.class.getSimpleName(), ResearchApplication.class.getSimpleName())
                .collect(Collectors.toMap(
                        Function.identity(),
                        name -> applications.stream()
                                .filter(a -> a.getClass().getSimpleName().equals(name))
                                .filter(scoreAtLeast(60))
                                .map(a -> a.getStudentId())
                                .distinct()
                                .count(),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }
}
