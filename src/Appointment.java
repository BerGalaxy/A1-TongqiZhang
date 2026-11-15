/**
 * Represents a single appointment for the current day.
 * <p>
 * An appointment links a patient to a health professional and a time slot.
 * Every field is final and is checked in the constructor, so an appointment can
 * never exist in an incomplete or invalid state.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class Appointment implements Comparable<Appointment> {

    /** The patient who booked the appointment. */
    private final Patient patient;

    /** The health professional who will see the patient. */
    private final HealthProfessional healthProfessional;

    /** The time slot of the appointment. */
    private final TimeSlot timeSlot;

    /**
     * Creates an appointment from a patient, a health professional and a time
     * slot.
     *
     * @param patient            the patient; must not be null
     * @param healthProfessional the chosen health professional; must not be null
     * @param timeSlot           the chosen time slot; must not be null
     * @throws IllegalArgumentException if any argument is null
     */
    public Appointment(Patient patient, HealthProfessional healthProfessional, TimeSlot timeSlot) {
        if (patient == null) {
            throw new IllegalArgumentException("An appointment must have a patient.");
        }
        if (healthProfessional == null) {
            throw new IllegalArgumentException("An appointment must have a health professional.");
        }
        if (timeSlot == null) {
            throw new IllegalArgumentException("An appointment must have a time slot.");
        }
        this.patient = patient;
        this.healthProfessional = healthProfessional;
        this.timeSlot = timeSlot;
    }

    /**
     * Returns the patient of this appointment.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Returns the health professional of this appointment.
     *
     * @return the health professional
     */
    public HealthProfessional getHealthProfessional() {
        return healthProfessional;
    }

    /**
     * Returns the time slot of this appointment.
     *
     * @return the time slot
     */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * Returns the details of this appointment in a readable form, including the
     * details of the chosen health professional.
     *
     * @return the appointment details
     */
    @Override
    public String toString() {
        return "Appointment\n"
                + "  Time          : " + timeSlot.getTimeRange() + "\n"
                + "  Patient       : " + patient + "\n"
                + "  Professional  : " + healthProfessional.getSummary();
    }

    /**
     * Compares this appointment with another appointment.
     * <p>
     * Appointments are ordered by time. When two appointments start at the same
     * time, the professional ID and then the patient name are used as
     * tie-breakers, so the order is always predictable.
     * </p>
     *
     * @param other the appointment to compare with
     * @return a negative number, zero or a positive number if this appointment
     *         is earlier than, equal to or later than the other appointment
     */
    @Override
    public int compareTo(Appointment other) {
        int timeOrder = timeSlot.compareTo(other.timeSlot);
        if (timeOrder != 0) {
            return timeOrder;
        }

        int professionalOrder = healthProfessional.getId().compareTo(other.healthProfessional.getId());
        if (professionalOrder != 0) {
            return professionalOrder;
        }

        // The remaining fields only make the order stable for equal time slots.
        return patient.getName().compareToIgnoreCase(other.patient.getName());
    }
}
