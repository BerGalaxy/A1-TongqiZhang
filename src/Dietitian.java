import java.util.List;

// Inherits from HealthProfessional.
public class Dietitian extends HealthProfessional {
    private String certification;

    public Dietitian() {
        super();
        this.certification = "Basic";
    }

    public Dietitian(String id, String name, List<String> availableDays, String certification) {
        super(id, name, availableDays);
        this.certification = certification;
    }

    // Specific behavior.
    public void createDietPlan() {
        System.out.println(getName() + " is creating a diet plan.");
    }

    @Override
    public String toString() {
        return "[Dietitian]\n" + super.toString() + "\nCertification: " + certification + "\n";
    }
}