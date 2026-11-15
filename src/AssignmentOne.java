import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Entry point of the health service appointment system.
 * <p>
 * The {@code main} method contains a scripted demonstration of the complete
 * system, as required by the brief. It creates the health professionals
 * (Parts 1 - 3), then books, filters, sorts and cancels appointments of different
 * types (Parts 4 - 6). Every operation prints its result, including the warning
 * messages for operations that cannot be completed.
 * </p>
 * <p>
 * After the demonstration, an optional interactive console is started so that
 * the system can also be used with real input.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class AssignmentOne {

    /** Line used to separate the sections of the demonstration. */
    private static final String LINE = "================================================================";

    /**
     * Prevents instantiation of this class, which only contains a
     * demonstration and helper methods.
     */
    private AssignmentOne() {
        // The class is used through its static methods only.
    }

    /**
     * Runs the scripted demonstration and then the optional console.
     *
     * @param args command line arguments; not used
     */
    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println(" PROG2004 Object Oriented Programming - Assignment 1");
        System.out.println(" Health Service Appointment System");
        System.out.println(LINE);

        List<HealthProfessional> professionals = createHealthProfessionals();
        demonstrateHealthProfessionals(professionals);

        AppointmentManager manager = new AppointmentManager();
        demonstrateAppointmentSystem(manager, professionals);

        System.out.println();
        System.out.println(LINE);
        System.out.println(" Scripted demonstration complete.");
        System.out.println(" The optional interactive console starts below.");
        System.out.println(LINE);

        AppointmentConsole console = new AppointmentConsole(manager, professionals);
        console.run();
    }

    /**
     * Creates the health professionals that the clinic employs.
     * <p>
     * All objects are stored through the abstract type
     * {@link HealthProfessional}, so the different types can be processed
     * together.
     * </p>
     *
     * @return the list of health professionals
     */
    private static List<HealthProfessional> createHealthProfessionals() {
        List<HealthProfessional> professionals = new ArrayList<>();

        professionals.add(new GeneralPractitioner("1001", "Dr Alice Chen",
                Arrays.asList("Monday", "Wednesday", "Friday"), "Room 12"));
        professionals.add(new GeneralPractitioner("1002", "Dr Brian Lee",
                Arrays.asList("Tuesday", "Thursday"), "Room 8"));
        professionals.add(new GeneralPractitioner("1003", "Dr Carol Diaz",
                Arrays.asList("Monday", "Tuesday", "Thursday"), "Room 15"));

        professionals.add(new Nurse("2001", "Nurse Emma Wilson",
                Arrays.asList("Monday", "Tuesday", "Friday"), "Outpatient Department"));
        professionals.add(new Nurse("2002", "Nurse Liam Patel",
                Arrays.asList("Wednesday", "Thursday", "Saturday"), "Emergency Department"));

        professionals.add(new Surgeon("3001", "Dr Nora Ibrahim",
                Arrays.asList("Tuesday", "Friday"), "Orthopaedic Surgery"));

        professionals.add(new Dietitian("4001", "Ms Grace Kim",
                Arrays.asList("Wednesday", "Thursday"), "Clinical Nutrition"));

        return professionals;
    }

    /**
     * Demonstrates Parts 1 - 3: the professionals are printed through the
     * common type, and each object supplies its own details and duty.
     *
     * @param professionals the health professionals to display
     */
    private static void demonstrateHealthProfessionals(List<HealthProfessional> professionals) {
        printSection("PARTS 1 - 3: HEALTH PROFESSIONALS");

        System.out.println("The clinic employs " + professionals.size() + " health professionals.");
        System.out.println("Every object below is handled through the type HealthProfessional.");
        System.out.println();

        int number = 1;
        for (HealthProfessional professional : professionals) {
            System.out.println("--- Health professional " + number + " ---");
            System.out.println(professional);
            System.out.println("Duty: " + professional.performCoreDuty("Sample Patient"));
            System.out.println();
            number++;
        }
    }

    /**
     * Demonstrates Parts 4 - 6: appointments are booked, displayed, filtered,
     * sorted and cancelled, and the error cases are shown as well.
     *
     * @param manager       the appointment manager to use
     * @param professionals the health professionals who can be booked
     */
    private static void demonstrateAppointmentSystem(AppointmentManager manager,
            List<HealthProfessional> professionals) {
        printSection("PARTS 4 - 6: APPOINTMENTS");

        Patient john = new Patient("John Smith", "0412 345 678");
        Patient mary = new Patient("Mary Johnson", "0423 456 789");
        Patient david = new Patient("David Brown", "0434 567 890");
        Patient sophie = new Patient("Sophie Taylor", "0445 678 901");

        System.out.println("Patients registered for today:");
        System.out.println("  " + john);
        System.out.println("  " + mary);
        System.out.println("  " + david);
        System.out.println("  " + sophie);

        System.out.println();
        System.out.println(">>> 6.1 Booking appointments of different health professional types");
        book(manager, john, findProfessional(professionals, "1001"), "09:00");
        book(manager, mary, findProfessional(professionals, "2001"), "09:30");
        book(manager, david, findProfessional(professionals, "3001"), "10:00");
        book(manager, sophie, findProfessional(professionals, "1001"), "10:00");
        book(manager, john, findProfessional(professionals, "4001"), "11:00");
        book(manager, mary, findProfessional(professionals, "1002"), "11:30");

        System.out.println();
        System.out.println(">>> 6.2 Error case: double-booking the same professional at the same time");
        book(manager, john, findProfessional(professionals, "1001"), "09:00");

        System.out.println();
        System.out.println(">>> 6.3 Viewing all appointments in time order");
        manager.displayAppointments("ALL APPOINTMENTS (sorted by time)", manager.getAllAppointments());

        System.out.println();
        System.out.println(">>> 6.4 Filtering appointments by health professional ID");
        manager.displayAppointments("APPOINTMENTS FOR ID 1001 (sorted by time)",
                manager.getAppointmentsByProfessional("1001"));

        System.out.println();
        System.out.println(">>> 6.5 Filtering appointments by patient mobile number");
        manager.displayAppointments("APPOINTMENTS FOR 0423456789 (sorted by time)",
                manager.getAppointmentsByPatientPhone("0423456789"));

        System.out.println();
        System.out.println(">>> 6.6 Cancelling an appointment by health professional ID and time");
        System.out.println(manager.cancelAppointment("1001", TimeSlot.parse("10:00")));

        System.out.println();
        System.out.println(">>> 6.7 Cancelling an appointment by patient mobile number");
        System.out.println(manager.cancelAppointmentByPatientPhone("0423456789"));

        System.out.println();
        System.out.println(">>> 6.8 Error case: cancelling appointments that do not exist");
        System.out.println(manager.cancelAppointment("9999", TimeSlot.parse("14:00")));
        System.out.println(manager.cancelAppointmentByPatientPhone("0499999999"));

        System.out.println();
        System.out.println(">>> 6.9 Error case: booking with an invalid time slot");
        try {
            TimeSlot.parse("10:15");
            System.out.println("Unexpected success: 10:15 is not a clinic slot.");
        } catch (IllegalArgumentException exception) {
            System.out.println("WARNING: " + exception.getMessage());
        }

        System.out.println();
        System.out.println(">>> 6.10 Error case: creating an appointment with missing details");
        try {
            new Appointment(john, null, TimeSlot.parse("12:00"));
            System.out.println("Unexpected success: an appointment needs a health professional.");
        } catch (IllegalArgumentException exception) {
            System.out.println("WARNING: " + exception.getMessage());
        }
        try {
            new Patient("Test Patient", "12345");
            System.out.println("Unexpected success: 12345 is not a valid mobile number.");
        } catch (IllegalArgumentException exception) {
            System.out.println("WARNING: " + exception.getMessage());
        }

        System.out.println();
        System.out.println(">>> 6.11 Viewing the appointments after the cancellations");
        manager.displayAppointments("ALL APPOINTMENTS AFTER CANCELLATIONS (sorted by time)",
                manager.getAllAppointments());
    }

    /**
     * Books one appointment and prints the message returned by the manager.
     *
     * @param manager      the appointment manager to use
     * @param patient      the patient of the appointment
     * @param professional the chosen health professional
     * @param time         the start time of the appointment, for example "09:30"
     */
    private static void book(AppointmentManager manager, Patient patient,
            HealthProfessional professional, String time) {
        Appointment appointment = new Appointment(patient, professional, TimeSlot.parse(time));
        System.out.println(manager.addAppointment(appointment));
    }

    /**
     * Finds a health professional by ID.
     *
     * @param professionals the professionals to search
     * @param id            the ID to look for
     * @return the matching professional
     * @throws IllegalArgumentException if no professional has the ID
     */
    private static HealthProfessional findProfessional(List<HealthProfessional> professionals, String id) {
        for (HealthProfessional professional : professionals) {
            if (professional.getId().equals(id)) {
                return professional;
            }
        }
        throw new IllegalArgumentException("No health professional has ID " + id + ".");
    }

    /**
     * Prints a section heading for the demonstration.
     *
     * @param title the title of the section
     */
    private static void printSection(String title) {
        System.out.println();
        System.out.println(LINE);
        System.out.println(" " + title);
        System.out.println(LINE);
    }
}
