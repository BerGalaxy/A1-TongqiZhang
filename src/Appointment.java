// Class representing a single appointment booking.
public class Appointment implements Comparable<Appointment> {
    private String patientName;
    private String patientMobile;
    private String appointmentTime;
    private HealthProfessional healthProfessional; // Composition: linking to a health professional.

    // Default constructor.
    public Appointment() {
        this.patientName = "Unknown";
        this.patientMobile = "0000000000";
        this.appointmentTime = "00:00";
        this.healthProfessional = null;
    }

    // Parameterized constructor.
    public Appointment(String patientName, String patientMobile, String appointmentTime, HealthProfessional healthProfessional) {
        this.patientName = patientName;
        this.patientMobile = patientMobile;
        this.appointmentTime = appointmentTime;
        this.healthProfessional = healthProfessional;
    }

    // Getters and setters.
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientMobile() {
        return patientMobile;
    }

    public void setPatientMobile(String patientMobile) {
        this.patientMobile = patientMobile;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public HealthProfessional getHealthProfessional() {
        return healthProfessional;
    }

    public void setHealthProfessional(HealthProfessional healthProfessional) {
        this.healthProfessional = healthProfessional;
    }

    // Display appointment details clearly.
    @Override
    public String toString() {
        return "--- Appointment Details ---\n" +
               "Patient Name: " + patientName + "\n" +
               "Patient Mobile: " + patientMobile + "\n" +
               "Time: " + appointmentTime + "\n" +
               "Booked with: " + (healthProfessional != null ? healthProfessional.getName() : "No Professional Assigned") + "\n" +
               "---------------------------";
    }

    // Implement compareTo to sort appointments chronologically.
@Override
public int compareTo(Appointment other) {
    return this.appointmentTime.compareTo(other.appointmentTime);
    }
}