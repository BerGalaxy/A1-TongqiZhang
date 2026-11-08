import java.util.List;

// Inherits from HealthProfessional.
public class Surgeon extends HealthProfessional {
    private String specialty;

    public Surgeon() {
        super();
        this.specialty = "General Surgery";
    }

    public Surgeon(String id, String name, List<String> availableDays, String specialty) {
        super(id, name, availableDays);
        this.specialty = specialty;
    }

    // Specific behavior.
    public void performSurgery() {
        System.out.println(getName() + " is performing surgery in specialty: " + specialty);
    }

    @Override
    public String toString() {
        return "[Surgeon]\n" + super.toString() + "\nSpecialty: " + specialty + "\n";
    }
}