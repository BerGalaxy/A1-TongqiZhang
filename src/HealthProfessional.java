import java.util.List;

// Abstract class to prevent direct instantiation (Part 1 requirement).
public abstract class HealthProfessional {
    
    // Encapsulated attributes.
    private String id;
    private String name;
    private List<String> availableDays;

    // Default constructor providing default values.
    public HealthProfessional() {
        this.id = "0000";
        this.name = "Unknown";
        this.availableDays = List.of("Monday");
    }

    // Parameterized constructor.
    public HealthProfessional(String id, String name, List<String> availableDays) {
        setID(id); // Use setter to trigger validation.
        this.name = name;
        this.availableDays = availableDays;
    }

    // Getter and Setter for ID with validation logic.
    public String getID() {
        return id;
    }

    public void setID(String id) {
        // ID must contain numbers only.
        if (id != null && id.matches("\\d+")) {
            this.id = id;
        } else {
            System.out.println("Error: ID must be numeric. Defaulting to '0000'.");
            this.id = "0000";
        }
    }

    // Getters and Setters for other attributes.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }

    // Override toString() to display all details clearly.
    @Override
    public String toString() {
        return "Health Professional Details:\n" +
               "ID: " + id + "\n" +
               "Name: " + name + "\n" +
               "Available Days: " + availableDays;
    }

    // Override equals to compare health professionals based on their ID.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HealthProfessional that = (HealthProfessional) obj;
        return id.equals(that.id);
    }
    
    // Override hashCode along with equals (Java best practice).
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}