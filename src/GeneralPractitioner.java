import java.util.List;

// Inherits from HealthProfessional (Inheritance).
public class GeneralPractitioner extends HealthProfessional {
    // Specific attribute.
    private String clinicRoom;

    // Default constructor.
    public GeneralPractitioner() {
        super();
        this.clinicRoom = "Room 101";
    }

    // Parameterized constructor.
    public GeneralPractitioner(String id, String name, List<String> availableDays, String clinicRoom) {
        super(id, name, availableDays); // Call superclass constructor.
        this.clinicRoom = clinicRoom;
    }

    // Specific behavior.
    public void prescribeMedication(String medication) {
        System.out.println(getName() + " prescribed: " + medication);
    }

    // Override toString to include both shared and specific info (Polymorphism).
    @Override
    public String toString() {
        return "[General Practitioner]\n" + super.toString() + "\nClinic Room: " + clinicRoom + "\n";
    }
}