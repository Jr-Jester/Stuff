package models;

public class ResearchApplication extends ScholarshipApplication {
    private final int articles;
    private final int talks;

    public ResearchApplication(int id, int studentId, int articles, int talks) {
        super(id, studentId);
        if (articles < 0 || articles > 5) {
            throw new IllegalArgumentException("Articles must be between 0 and 5");
        }
        if (talks < 0 || talks > 10) {
            throw new IllegalArgumentException("Talks must be between 0 and 10");
        }
        this.articles = articles;
        this.talks = talks;
    }

    public int getArticles() {
        return articles;
    }

    public int getTalks() {
        return talks;
    }

    @Override
    public double calculateScore() {
        return Math.min(100, 20 * articles + 10 * talks);
    }

    @Override
    public String getDetails() {
        return super.getDetails() + String.format(" | articles %d, talks %d", articles, talks);
    }
}
