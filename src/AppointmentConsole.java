import java.util.List;
import java.util.Scanner;

/**
 * Provides an optional text-based console for booking, viewing and cancelling
 * appointments.
 * <p>
 * The assessed demonstration is the scripted simulation in
 * {@link AssignmentOne}. This class adds menu-driven interaction on top of it so
 * that the appointment system can be tried with real input. All input is
 * validated before it reaches the model classes, and every result is printed
 * with the message returned by {@link AppointmentManager}.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class AppointmentConsole {

    /** Line used to frame the menu. */
    private static final String LINE = "==================================================================";

    /** Application logic used by the menu. */
    private final AppointmentManager manager;

    /** Health professionals that can be booked. */
    private final List<HealthProfessional> professionals;

    /** Reads input from the console. */
    private final Scanner scanner;

    /**
     * Creates the console.
     *
     * @param manager       the appointment manager to use
     * @param professionals the health professionals who can be booked
     */
    public AppointmentConsole(AppointmentManager manager, List<HealthProfessional> professionals) {
        this.manager = manager;
        this.professionals = professionals;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Shows the menu until the user chooses to exit.
     */
    public void run() {
        printWelcome();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Choose an option: ");

            if (!scanner.hasNextLine()) {
                System.out.println();
                System.out.println("End of input reached. Closing the interactive console.");
                break;
            }

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    bookAppointment();
                    break;
                case "2":
                    manager.displayAppointments("ALL APPOINTMENTS (sorted by time)",
                            manager.getAllAppointments());
                    break;
                case "3":
                    viewAppointmentsByProfessional();
                    break;
                case "4":
                    viewAppointmentsByPatientPhone();
                    break;
                case "5":
                    cancelByProfessionalAndTime();
                    break;
                case "6":
                    cancelByPatientPhone();
                    break;
                case "7":
                    printDailySchedule();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("WARNING: Please choose a number from 0 to 7.");
                    break;
            }
        }

        scanner.close();
        System.out.println("The interactive console is closed. Thank you.");
    }

    /**
     * Prints a short introduction to the console.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println(LINE);
        System.out.println(" OPTIONAL INTERACTIVE CONSOLE");
        System.out.println(LINE);
        System.out.println("The scripted demonstration above already covers Parts 3 and 6");
        System.out.println("of the brief. This console lets you book, view and cancel");
        System.out.println("appointments with your own input. Every booking is kept in");
        System.out.println("memory for the rest of this session and can be viewed again");
        System.out.println("with options 2, 3 and 4.");
        System.out.println();
        System.out.println("Type 'cancel' at any prompt to return to the menu.");
    }

    /**
     * Prints the main menu and the current number of appointments.
     */
    private void printMenu() {
        System.out.println();
        System.out.println(LINE);
        System.out.println(" MAIN MENU");
        System.out.println(LINE);
        System.out.println(" 1. Book an appointment");
        System.out.println(" 2. View all appointments (sorted by time)");
        System.out.println(" 3. View appointments for a health professional");
        System.out.println(" 4. View my appointments (by mobile number)");
        System.out.println(" 5. Cancel an appointment (health professional ID and time)");
        System.out.println(" 6. Cancel an appointment (patient mobile number)");
        System.out.println(" 7. Show the daily schedule");
        System.out.println(" 0. Exit");
        System.out.println(LINE);
        System.out.println(" Appointments currently in the system: " + manager.getAppointmentCount());
    }

    /**
     * Asks for a patient, a health professional and a time, then books the
     * appointment.
     */
    private void bookAppointment() {
        System.out.println();
        System.out.println("--- Book an appointment ---");
        printProfessionalDirectory();

        Patient patient = readPatient();
        if (patient == null) {
            System.out.println("Booking cancelled.");
            return;
        }

        HealthProfessional professional = readProfessional("Health professional ID: ");
        if (professional == null) {
            System.out.println("Booking cancelled.");
            return;
        }

        TimeSlot timeSlot = readTimeSlot();
        if (timeSlot == null) {
            System.out.println("Booking cancelled.");
            return;
        }

        String result = manager.addAppointment(new Appointment(patient, professional, timeSlot));
        System.out.println(result);

        if (result.startsWith("SUCCESS")) {
            System.out.println("This booking is now saved. Use option 4 with mobile number "
                    + patient.getMobile() + " to see all appointments for this patient.");
        }
    }

    /**
     * Shows the appointments of one health professional.
     */
    private void viewAppointmentsByProfessional() {
        System.out.println();
        printProfessionalDirectory();

        HealthProfessional professional = readProfessional("Health professional ID: ");
        if (professional == null) {
            System.out.println("No appointments were displayed.");
            return;
        }

        manager.displayAppointments("APPOINTMENTS FOR " + professional.getSummary() + " (sorted by time)",
                manager.getAppointmentsByProfessional(professional.getId()));
    }

    /**
     * Shows the appointments that belong to one patient's mobile number.
     */
    private void viewAppointmentsByPatientPhone() {
        System.out.println();
        String mobile = readMobile("Patient mobile number: ");
        if (mobile == null) {
            System.out.println("No appointments were displayed.");
            return;
        }

        manager.displayAppointments("APPOINTMENTS FOR MOBILE " + mobile + " (sorted by time)",
                manager.getAppointmentsByPatientPhone(mobile));
    }

    /**
     * Cancels an appointment by health professional ID and time.
     */
    private void cancelByProfessionalAndTime() {
        System.out.println();
        printProfessionalDirectory();

        HealthProfessional professional = readProfessional("Health professional ID: ");
        if (professional == null) {
            System.out.println("No appointment was cancelled.");
            return;
        }

        TimeSlot timeSlot = readTimeSlot();
        if (timeSlot == null) {
            System.out.println("No appointment was cancelled.");
            return;
        }

        System.out.println(manager.cancelAppointment(professional.getId(), timeSlot));
    }

    /**
     * Cancels the earliest appointment that belongs to a patient's mobile
     * number.
     */
    private void cancelByPatientPhone() {
        System.out.println();
        String mobile = readMobile("Patient mobile number: ");
        if (mobile == null) {
            System.out.println("No appointment was cancelled.");
            return;
        }

        System.out.println(manager.cancelAppointmentByPatientPhone(mobile));
    }

    /**
     * Reads a complete patient record, asking again until the details are valid.
     *
     * @return the patient, or {@code null} if the user typed "cancel"
     */
    private Patient readPatient() {
        while (true) {
            String name = readText("Patient name: ");
            if (name == null) {
                return null;
            }

            String mobile = readText("Patient mobile (10 digits, for example 0412345678): ");
            if (mobile == null) {
                return null;
            }

            try {
                return new Patient(name, mobile);
            } catch (IllegalArgumentException exception) {
                System.out.println("WARNING: " + exception.getMessage());
            }
        }
    }

    /**
     * Reads a health professional ID and finds the matching object.
     *
     * @param prompt the text shown before the input
     * @return the matching professional, or {@code null} if the user typed "cancel"
     */
    private HealthProfessional readProfessional(String prompt) {
        while (true) {
            String id = readText(prompt);
            if (id == null) {
                return null;
            }

            for (HealthProfessional professional : professionals) {
                if (professional.getId().equals(id)) {
                    return professional;
                }
            }
            System.out.println("WARNING: No health professional has ID " + id + ". Please try again.");
        }
    }

    /**
     * Reads a valid appointment time.
     *
     * @return the time slot, or {@code null} if the user typed "cancel"
     */
    private TimeSlot readTimeSlot() {
        printDailySchedule();

        while (true) {
            String text = readText("Appointment time (HH:mm): ");
            if (text == null) {
                return null;
            }

            try {
                return TimeSlot.parse(text);
            } catch (IllegalArgumentException exception) {
                System.out.println("WARNING: " + exception.getMessage());
            }
        }
    }

    /**
     * Reads and validates a mobile number.
     *
     * @param prompt the text shown before the input
     * @return the mobile number as 10 digits, or {@code null} if the user typed "cancel"
     */
    private String readMobile(String prompt) {
        while (true) {
            String text = readText(prompt);
            if (text == null) {
                return null;
            }

            try {
                return Patient.normalizeMobile(text);
            } catch (IllegalArgumentException exception) {
                System.out.println("WARNING: " + exception.getMessage());
            }
        }
    }

    /**
     * Reads one line of text and handles the "cancel" keyword and empty input.
     *
     * @param prompt the text shown before the input
     * @return the trimmed input, or {@code null} if the user typed "cancel" or input ended
     */
    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return null;
            }

            String value = scanner.nextLine().trim();
            if (value.equalsIgnoreCase("cancel")) {
                return null;
            }
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("WARNING: Input cannot be empty. Type 'cancel' to go back.");
        }
    }

    /**
     * Prints the IDs, names and types of all bookable professionals.
     */
    private void printProfessionalDirectory() {
        System.out.println("Available health professionals:");
        for (HealthProfessional professional : professionals) {
            System.out.println("  " + professional.getId() + " - " + professional.getName()
                    + " (" + professional.getProfessionalType() + ")");
        }
    }

    /**
     * Prints the daily schedule in four columns.
     */
    private void printDailySchedule() {
        System.out.println("Clinic schedule (" + String.format("%02d:00", TimeSlot.CLINIC_OPENING_HOUR)
                + " - " + String.format("%02d:00", TimeSlot.CLINIC_CLOSING_HOUR) + ", "
                + TimeSlot.SLOT_LENGTH_MINUTES + "-minute slots):");

        List<TimeSlot> schedule = TimeSlot.getDailySchedule();
        for (int index = 0; index < schedule.size(); index++) {
            System.out.print(schedule.get(index).getTimeRange() + "   ");
            if ((index + 1) % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }
}
