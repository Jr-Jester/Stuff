package main;

import logic.ScholarshipManager;
import models.AcademicApplication;
import models.ResearchApplication;
import models.ScholarshipApplication;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ScholarshipManager<ScholarshipApplication> manager = createInitialManager();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        do {
            printMenu();
            try {
                int choice = readInt(scanner, "Choose an option: ");
                switch (choice) {
                    case 1:
                        addApplication(scanner, manager);
                        break;
                    case 2:
                        showApplications(manager);
                        break;
                    case 3:
                        runCompetition(scanner, manager);
                        break;
                    case 4:
                        System.out.println("Analytics result: " + manager.analytics_1());
                        break;
                    case 5:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Error: unknown menu item, choose 1-5.");
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

    private static ScholarshipManager<ScholarshipApplication> createInitialManager() {
        ScholarshipManager<ScholarshipApplication> manager = new ScholarshipManager<>();
        manager.add(new AcademicApplication(1, 1, 100, 100));
        manager.add(new AcademicApplication(3, 2, 85, 85));
        manager.add(new AcademicApplication(4, 2, 85, 85));
        manager.add(new AcademicApplication(5, 3, 80, 80));
        manager.add(new AcademicApplication(7, 4, 60, 60));
        manager.add(new AcademicApplication(9, 5, 59, 59));
        manager.add(new AcademicApplication(11, 6, 90, 90));
        manager.add(new AcademicApplication(13, 7, 100, 100));
        manager.add(new ResearchApplication(2, 1, 3, 0));
        manager.add(new ResearchApplication(6, 3, 4, 0));
        manager.add(new ResearchApplication(8, 4, 3, 0));
        manager.add(new ResearchApplication(10, 5, 5, 0));
        manager.add(new ResearchApplication(12, 6, 4, 1));
        return manager;
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Scholarships =====");
        System.out.println("1. Add application");
        System.out.println("2. Show all applications");
        System.out.println("3. Run competition");
        System.out.println("4. Analytics");
        System.out.println("5. Exit");
    }

    private static int readInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private static void addApplication(Scanner scanner, ScholarshipManager<ScholarshipApplication> manager) {
        int type = readInt(scanner, "Application type (1 - Academic, 2 - Research): ");
        if (type != 1 && type != 2) {
            System.out.println("Error: unknown application type.");
            return;
        }
        int id = readInt(scanner, "Application id: ");
        int studentId = readInt(scanner, "Student id: ");
        ScholarshipApplication application;
        if (type == 1) {
            int exam1 = readInt(scanner, "Exam 1 (0-100): ");
            int exam2 = readInt(scanner, "Exam 2 (0-100): ");
            application = new AcademicApplication(id, studentId, exam1, exam2);
        } else {
            int articles = readInt(scanner, "Articles (0-5): ");
            int talks = readInt(scanner, "Talks (0-10): ");
            application = new ResearchApplication(id, studentId, articles, talks);
        }
        if (manager.add(application)) {
            System.out.println("Application added: " + application.getDetails());
        } else {
            System.out.println("Error: application with id " + id + " already exists.");
        }
    }

    private static void showApplications(ScholarshipManager<ScholarshipApplication> manager) {
        if (manager.getAnalyticsStream().count() == 0) {
            System.out.println("No applications.");
        } else {
            manager.getAnalyticsStream().map(ScholarshipApplication::getDetails).forEach(System.out::println);
        }
    }

    private static void runCompetition(Scanner scanner, ScholarshipManager<ScholarshipApplication> manager) {
        int limit = readInt(scanner, "Number of scholarships (limit): ");
        List<ScholarshipApplication> winners = manager.allocate(limit);
        if (winners.isEmpty()) {
            System.out.println("No winners.");
            return;
        }
        System.out.println("Winners:");
        int place = 1;
        for (ScholarshipApplication winner : winners) {
            double rating = manager.getStudentRating(winner.getStudentId()).getAsDouble();
            System.out.printf("%d. student %d | rating %.2f | application id %d%n",
                    place, winner.getStudentId(), rating, winner.getId());
            place++;
        }
    }
}
