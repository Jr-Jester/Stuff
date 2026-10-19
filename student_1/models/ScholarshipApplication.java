package student_1.models;

import student_1.interfaces.Scorable;

public abstract class ScholarshipApplication implements Scorable {
    private final int id;
    private final int studentId;
    private boolean awarded;

    protected ScholarshipApplication(int id, int studentId) {
        if (id <= 0) {
            throw new IllegalArgumentException("Application id must be a positive number");
        }
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student id must be a positive number");
        }
        this.id = id;
        this.studentId = studentId;
        this.awarded = false;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public boolean isAwarded() {
        return awarded;
    }

    public void setAwarded(boolean awarded) {
        this.awarded = awarded;
    }

    public String getDetails() {
        return String.format("%-19s | id %3d | student %3d | score %6.2f | %s",
                getClass().getSimpleName(), id, studentId, calculateScore(),
                awarded ? "AWARDED" : "not awarded");
    }

    @Override
    public String toString() {
        return getDetails();
    }
}
