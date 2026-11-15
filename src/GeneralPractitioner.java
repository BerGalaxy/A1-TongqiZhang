import java.util.List;

/**
 * Represents a general practitioner.
 * <p>
 * A general practitioner is a doctor who sees patients in a consulting room and
 * can prescribe medication. The class adds the consulting room to the details
 * that every health professional already has.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class GeneralPractitioner extends HealthProfessional {

    /** Room in which the general practitioner sees patients. */
    private String consultingRoom;

    /**
     * Creates a general practitioner with default values.
     */
    public GeneralPractitioner() {
        super();
        this.consultingRoom = "Room 1";
    }

    /**
     * Creates a general practitioner with the supplied details.
     *
     * @param id            unique identifier; digits only
     * @param name          full name; must not be blank
     * @param availableDays working days; must not be empty
     * @param consultingRoom room in which patients are seen; must not be blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public GeneralPractitioner(String id, String name, List<String> availableDays, String consultingRoom) {
        super(id, name, availableDays);
        this.consultingRoom = validateConsultingRoom(consultingRoom);
    }

    /**
     * Returns the room in which this general practitioner sees patients.
     *
     * @return the consulting room
     */
    public String getConsultingRoom() {
        return consultingRoom;
    }

    /**
     * Changes the consulting room of this general practitioner.
     *
     * @param consultingRoom new room; must not be blank
     * @throws IllegalArgumentException if the room is null or blank
     */
    public void setConsultingRoom(String consultingRoom) {
        this.consultingRoom = validateConsultingRoom(consultingRoom);
    }

    /**
     * Checks a consulting room and returns it in a clean form.
     *
     * @param consultingRoom the room to check
     * @return the room without surrounding spaces
     * @throws IllegalArgumentException if the room is null or blank
     */
    private static String validateConsultingRoom(String consultingRoom) {
        if (consultingRoom == null || consultingRoom.trim().isEmpty()) {
            throw new IllegalArgumentException("Consulting room must not be empty.");
        }
        return consultingRoom.trim();
    }

    /**
     * Returns the type of this health professional.
     *
     * @return the text "General Practitioner"
     */
    @Override
    public String getProfessionalType() {
        return "General Practitioner";
    }

    /**
     * Prescribes medication for a patient.
     *
     * @param patientName name of the patient
     * @return a description of the duty that was performed
     */
    @Override
    public String performCoreDuty(String patientName) {
        return getName() + " prescribes medication for " + patientName + ".";
    }

    /**
     * Returns the shared details of the professional followed by the consulting
     * room.
     *
     * @return the details of this general practitioner
     */
    @Override
    public String toString() {
        return super.toString() + "\nConsulting room   : " + consultingRoom;
    }
}
