package models;

public class AcademicApplication extends ScholarshipApplication {
    private final int exam1;
    private final int exam2;

    public AcademicApplication(int id, int studentId, int exam1, int exam2) {
        super(id, studentId);
        if (exam1 < 0 || exam1 > 100) {
            throw new IllegalArgumentException("Exam 1 must be between 0 and 100");
        }
        if (exam2 < 0 || exam2 > 100) {
            throw new IllegalArgumentException("Exam 2 must be between 0 and 100");
        }
        this.exam1 = exam1;
        this.exam2 = exam2;
    }

    public int getExam1() {
        return exam1;
    }

    public int getExam2() {
        return exam2;
    }

    @Override
    public double calculateScore() {
        return (exam1 + exam2) / 2.0;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + String.format(" | exams %d, %d", exam1, exam2);
    }
}
