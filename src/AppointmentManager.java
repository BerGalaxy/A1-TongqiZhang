import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class to manage a collection of appointments.
public class AppointmentManager {
    private List<Appointment> appointments;

    public AppointmentManager() {
        this.appointments = new ArrayList<>();
    }

    // Add an appointment with conflict checking.
    public void addAppointment(Appointment newAppt) {
        // Check if the health professional is already booked at this time.
        for (Appointment existingAppt : appointments) {
            if (existingAppt.getHealthProfessional().equals(newAppt.getHealthProfessional()) &&
                existingAppt.getAppointmentTime().equals(newAppt.getAppointmentTime())) {
                System.out.println("Error: " + newAppt.getHealthProfessional().getName() + 
                                   " is already booked at " + newAppt.getAppointmentTime() + "!");
                return; // Exit method, do not add.
            }
        }
        appointments.add(newAppt);
        System.out.println("Success: Appointment added for " + newAppt.getPatientName());
    }

    // Cancel an appointment based on patient name and time.
    public void cancelAppointment(String patientName, String time) {
        boolean removed = false;
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appt = appointments.get(i);
            if (appt.getPatientName().equalsIgnoreCase(patientName) && appt.getAppointmentTime().equals(time)) {
                appointments.remove(i);
                System.out.println("Success: Cancelled appointment for " + patientName + " at " + time);
                removed = true;
                break;
            }
        }
        if (!removed) {
            System.out.println("Error: No appointment found for " + patientName + " at " + time + " to cancel.");
        }
    }

    // Display all appointments sorted by time.
    public void displayAllAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No appointments booked.");
            return;
        }
        
        // Sort the list based on the compareTo method we wrote in Appointment.
        Collections.sort(appointments);
        
        System.out.println("\n--- All Appointments (Sorted by Time) ---");
        for (Appointment appt : appointments) {
            System.out.println(appt.toString());
        }
    }

        // Filter and display appointments for a specific health professional.
    public void filterAppointmentsByProfessional(HealthProfessional hp) {
        System.out.println("\n--- Appointments for " + hp.getName() + " ---");
        boolean found = false;
        for (Appointment appt : appointments) {
            // Use our overridden equals method to check if it's the same professional.
            if (appt.getHealthProfessional().equals(hp)) {
                System.out.println(appt.toString());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No appointments found for " + hp.getName());
        }
    }
}