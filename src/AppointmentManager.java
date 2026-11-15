import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the collection of appointments for the current day.
 * <p>
 * The manager stores every appointment in an {@link ArrayList}, rejects
 * bookings that would double-book a health professional, and returns a warning
 * message whenever an operation cannot be completed. An {@code ArrayList} is
 * used because appointments are added and removed often during the day, the
 * number of bookings changes constantly, and the list keeps the natural
 * insertion order that the sorted views can copy from. Filtering and sorting
 * are done on copies of the stored appointments, so the original booking order
 * is never changed by a read-only operation.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class AppointmentManager {

    /** Line used to separate appointments in the console output. */
    private static final String SEPARATOR = "----------------------------------------------------------------";

    /** All appointments that are currently booked. */
    private final List<Appointment> appointments;

    /**
     * Creates an empty appointment manager.
     */
    public AppointmentManager() {
        this.appointments = new ArrayList<>();
    }

    /**
     * Adds a new appointment after checking that the health professional is
     * free at that time.
     *
     * @param appointment the appointment to add
     * @return a success message, or a warning message if the appointment could
     *         not be added
     */
    public String addAppointment(Appointment appointment) {
        if (appointment == null) {
            return "WARNING: The appointment is empty and was not added.";
        }

        HealthProfessional professional = appointment.getHealthProfessional();
        TimeSlot timeSlot = appointment.getTimeSlot();

        // A professional can never be in two places at once, so reject clashes.
        if (isProfessionalBooked(professional, timeSlot)) {
            return "WARNING: " + professional.getSummary() + " is already booked at "
                    + timeSlot.getTimeRange() + ". The appointment was not added.";
        }

        appointments.add(appointment);
        return "SUCCESS: " + appointment.getPatient().getName() + " is booked with "
                + professional.getSummary() + " at " + timeSlot.getTimeRange() + ".";
    }

    /**
     * Checks whether a health professional is already booked in a time slot.
     *
     * @param professional the professional to check; must not be null
     * @param timeSlot     the time slot to check; must not be null
     * @return {@code true} if the professional already has an appointment in that slot
     */
    public boolean isProfessionalBooked(HealthProfessional professional, TimeSlot timeSlot) {
        for (Appointment existing : appointments) {
            // equals() compares professionals by ID, so a clash is detected even
            // when the same ID is held by a different concrete type.
            boolean sameProfessional = existing.getHealthProfessional().equals(professional);
            boolean sameTime = existing.getTimeSlot().equals(timeSlot);
            if (sameProfessional && sameTime) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cancels the appointment of a health professional in a time slot.
     *
     * @param professionalId the ID of the health professional
     * @param timeSlot       the time slot of the appointment
     * @return a success message, or a warning message if no matching appointment exists
     */
    public String cancelAppointment(String professionalId, TimeSlot timeSlot) {
        if (professionalId == null || timeSlot == null) {
            return "WARNING: A health professional ID and a time are needed to cancel an appointment.";
        }

        for (int index = 0; index < appointments.size(); index++) {
            Appointment appointment = appointments.get(index);
            boolean sameProfessional = appointment.getHealthProfessional().getId().equals(professionalId);
            boolean sameTime = appointment.getTimeSlot().equals(timeSlot);
            if (sameProfessional && sameTime) {
                // Remove by index so the removed appointment can be used in the message.
                appointments.remove(index);
                return "SUCCESS: Cancelled the appointment of " + appointment.getPatient().getName()
                        + " with " + appointment.getHealthProfessional().getSummary()
                        + " at " + timeSlot.getTimeRange() + ".";
            }
        }

        return "WARNING: No appointment was found for health professional ID " + professionalId
                + " at " + timeSlot.getTimeRange() + ".";
    }

    /**
     * Cancels the earliest appointment that belongs to a patient's mobile
     * number.
     *
     * @param mobile the patient's mobile number, as 10 digits
     * @return a success message, or a warning message if the patient has no appointment
     */
    public String cancelAppointmentByPatientPhone(String mobile) {
        if (mobile == null) {
            return "WARNING: A patient mobile number is needed to cancel an appointment.";
        }

        Appointment earliest = null;
        for (Appointment appointment : appointments) {
            if (appointment.getPatient().getMobile().equals(mobile)) {
                // Keep the earliest booking in case the patient has more than one.
                if (earliest == null || appointment.getTimeSlot().compareTo(earliest.getTimeSlot()) < 0) {
                    earliest = appointment;
                }
            }
        }

        if (earliest == null) {
            return "WARNING: No appointment was found for patient mobile number " + mobile + ".";
        }

        appointments.remove(earliest);
        return "SUCCESS: Cancelled " + earliest.getPatient().getName() + "'s earliest appointment with "
                + earliest.getHealthProfessional().getSummary() + " at "
                + earliest.getTimeSlot().getTimeRange() + ".";
    }

    /**
     * Returns all appointments sorted by time.
     *
     * @return a sorted copy of all appointments
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> sorted = new ArrayList<>(appointments);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Returns the appointments of one health professional, sorted by time.
     *
     * @param professionalId the ID of the health professional
     * @return a sorted list of matching appointments, which may be empty
     */
    public List<Appointment> getAppointmentsByProfessional(String professionalId) {
        List<Appointment> matches = new ArrayList<>();
        if (professionalId == null) {
            return matches;
        }

        for (Appointment appointment : appointments) {
            if (appointment.getHealthProfessional().getId().equals(professionalId)) {
                matches.add(appointment);
            }
        }
        Collections.sort(matches);
        return matches;
    }

    /**
     * Returns the appointments of one patient, sorted by time.
     *
     * @param mobile the patient's mobile number, as 10 digits
     * @return a sorted list of matching appointments, which may be empty
     */
    public List<Appointment> getAppointmentsByPatientPhone(String mobile) {
        List<Appointment> matches = new ArrayList<>();
        if (mobile == null) {
            return matches;
        }

        for (Appointment appointment : appointments) {
            if (appointment.getPatient().getMobile().equals(mobile)) {
                matches.add(appointment);
            }
        }
        Collections.sort(matches);
        return matches;
    }

    /**
     * Returns the number of appointments that are currently booked.
     *
     * @return the number of appointments
     */
    public int getAppointmentCount() {
        return appointments.size();
    }

    /**
     * Prints a list of appointments under a heading.
     *
     * @param heading the heading that describes the list
     * @param list    the appointments to print; the list is expected to be sorted already
     */
    public void displayAppointments(String heading, List<Appointment> list) {
        System.out.println();
        System.out.println(heading);
        System.out.println(SEPARATOR);

        if (list.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        for (Appointment appointment : list) {
            System.out.println(appointment);
            System.out.println(SEPARATOR);
        }
        System.out.println("Total: " + list.size() + " appointment(s).");
    }
}
