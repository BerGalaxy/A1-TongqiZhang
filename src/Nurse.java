import java.util.List;

// Inherits from HealthProfessional.
public class Nurse extends HealthProfessional {
    private String department;

    public Nurse() {
        super();
        this.department = "General";
    }

    public Nurse(String id, String name, List<String> availableDays, String department) {
        super(id, name, availableDays);
        this.department = department;
    }

    // Specific behavior.
    public void takeBloodSample() {
        System.out.println(getName() + " is taking a blood sample.");
    }

    @Override
    public String toString() {
        return "[Nurse]\n" + super.toString() + "\nDepartment: " + department + "\n";
    }
}