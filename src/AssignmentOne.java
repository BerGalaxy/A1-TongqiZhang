import java.util.List;

public class AssignmentOne {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   PROG2004 Assignment 1: Final Demo     ");
        System.out.println("=========================================\n");

        // --- 1. Setup Health Professionals ---
        System.out.println(">>> 1. Creating Health Professionals");
        GeneralPractitioner gp1 = new GeneralPractitioner("101", "Dr. Alice", List.of("Mon", "Wed"), "Room 201");
        GeneralPractitioner gp2 = new GeneralPractitioner("102", "Dr. Bob", List.of("Tue", "Thu"), "Room 202");
        Nurse nurse1 = new Nurse("201", "Nurse Joy", List.of("Mon", "Fri"), "Pediatrics");
        Surgeon surgeon1 = new Surgeon("301", "Dr. Strange", List.of("Wed"), "Neurosurgery");
        Dietitian dietitian1 = new Dietitian("401", "Ms. Green", List.of("Thu"), "Advanced");

        //In order to eliminate the warning of unused variables, we printed all employees by the way (using polymorphism).
        List<HealthProfessional> staffList = List.of(gp1, gp2, nurse1, surgeon1, dietitian1);
        System.out.println("--- Clinic Staff List ---");
        for (HealthProfessional staff : staffList) {
            System.out.println("Name: " + staff.getName() + " | Type: " + staff.getClass().getSimpleName());
        }
        System.out.println("Professionals created successfully.\n");

        // --- 2. Setup Appointment Manager ---
        AppointmentManager manager = new AppointmentManager();

        // --- 3. Demonstrate Normal Cases ---
        System.out.println(">>> 2. Booking Appointments (Normal Cases)");
        Appointment appt1 = new Appointment("John Doe", "0411111111", "10:00", gp1);
        Appointment appt2 = new Appointment("Jane Smith", "0422222222", "09:00", nurse1);
        Appointment appt3 = new Appointment("Tom Hanks", "0433333333", "14:00", gp1);
        Appointment appt4 = new Appointment("Emma Stone", "0444444444", "11:30", surgeon1);
        
        manager.addAppointment(appt1);
        manager.addAppointment(appt2);
        manager.addAppointment(appt3);
        manager.addAppointment(appt4);

        // --- 4. Demonstrate Error Cases ---
        System.out.println("\n>>> 3. Testing Error Cases");
        // Error Case A: Booking the same doctor at the same time.
        Appointment conflictAppt = new Appointment("Bad Guy", "0400000000", "10:00", gp1);
        manager.addAppointment(conflictAppt); // Should fail.
        
        // Error Case B: Cancelling a non-existent appointment.
        manager.cancelAppointment("Ghost", "10:00"); // Should fail.

        // --- 5. Display All (Sorted) ---
        System.out.println("\n>>> 4. Displaying All Appointments (Sorted by Time)");
        manager.displayAllAppointments();

        // --- 6. Demonstrate Filtering ---
        System.out.println("\n>>> 5. Filtering Appointments by Professional");
        manager.filterAppointmentsByProfessional(gp1);

        // --- 7. Demonstrate Cancellation and Update ---
        System.out.println("\n>>> 6. Cancelling an Appointment and Updating List");
        manager.cancelAppointment("John Doe", "10:00");
        
        System.out.println("\n>>> 7. Final State of Appointments");
        manager.displayAllAppointments();
        
        System.out.println("\n=========================================");
        System.out.println("              Demo Finished              ");
        System.out.println("=========================================");
    }
}