import java.util.List;

/**
 * Represents a surgeon.
 * <p>
 * A surgeon performs operations and works in a surgical specialty such as
 * orthopaedic surgery. The class adds the specialty to the details that every
 * health professional already has.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class Surgeon extends HealthProfessional {

    /** Surgical specialty of the surgeon. */
    private String surgicalSpecialty;

    /**
     * Creates a surgeon with default values.
     */
    public Surgeon() {
        super();
        this.surgicalSpecialty = "General Surgery";
    }

    /**
     * Creates a surgeon with the supplied details.
     *
     * @param id              unique identifier; digits only
     * @param name            full name; must not be blank
     * @param availableDays   working days; must not be empty
     * @param surgicalSpecialty surgical specialty; must not be blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Surgeon(String id, String name, List<String> availableDays, String surgicalSpecialty) {
        super(id, name, availableDays);
        this.surgicalSpecialty = validateSurgicalSpecialty(surgicalSpecialty);
    }

    /**
     * Returns the surgical specialty of this surgeon.
     *
     * @return the surgical specialty
     */
    public String getSurgicalSpecialty() {
        return surgicalSpecialty;
    }

    /**
     * Changes the surgical specialty of this surgeon.
     *
     * @param surgicalSpecialty new specialty; must not be blank
     * @throws IllegalArgumentException if the specialty is null or blank
     */
    public void setSurgicalSpecialty(String surgicalSpecialty) {
        this.surgicalSpecialty = validateSurgicalSpecialty(surgicalSpecialty);
    }

    /**
     * Checks a surgical specialty and returns it in a clean form.
     *
     * @param surgicalSpecialty the specialty to check
     * @return the specialty without surrounding spaces
     * @throws IllegalArgumentException if the specialty is null or blank
     */
    private static String validateSurgicalSpecialty(String surgicalSpecialty) {
        if (surgicalSpecialty == null || surgicalSpecialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Surgical specialty must not be empty.");
        }
        return surgicalSpecialty.trim();
    }

    /**
     * Returns the type of this health professional.
     *
     * @return the text "Surgeon"
     */
    @Override
    public String getProfessionalType() {
        return "Surgeon";
    }

    /**
     * Performs a surgical procedure on a patient.
     *
     * @param patientName name of the patient
     * @return a description of the duty that was performed
     */
    @Override
    public String performCoreDuty(String patientName) {
        return getName() + " performs surgery in " + surgicalSpecialty + " on " + patientName + ".";
    }

    /**
     * Returns the shared details of the professional followed by the surgical
     * specialty.
     *
     * @return the details of this surgeon
     */
    @Override
    public String toString() {
        return super.toString() + "\nSurgical specialty: " + surgicalSpecialty;
    }
}
