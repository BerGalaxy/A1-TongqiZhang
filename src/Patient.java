/**
 * Represents a patient who books appointments at the clinic.
 * <p>
 * A patient is modelled as its own class because the same patient can book more
 * than one appointment, and because an appointment should link to a complete
 * patient record rather than to separate pieces of text.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class Patient {

    /** Name of the patient. */
    private final String name;

    /** Mobile number of the patient, stored as digits only. */
    private final String mobile;

    /**
     * Creates a patient with the supplied details.
     *
     * @param name   full name; must not be blank
     * @param mobile mobile number; 10 digits starting with 0, spaces and hyphens are ignored
     * @throws IllegalArgumentException if the name or the mobile number is invalid
     */
    public Patient(String name, String mobile) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name must not be empty.");
        }
        this.name = name.trim();
        this.mobile = normalizeMobile(mobile);
    }

    /**
     * Removes spaces and hyphens from a mobile number and checks that the
     * result is a valid Australian mobile number.
     *
     * @param mobile the mobile number supplied by the caller
     * @return the mobile number as 10 digits
     * @throws IllegalArgumentException if the mobile number is missing or has the wrong format
     */
    public static String normalizeMobile(String mobile) {
        if (mobile == null) {
            throw new IllegalArgumentException("Patient mobile number must not be empty.");
        }
        String digits = mobile.trim().replace(" ", "").replace("-", "");
        if (!digits.matches("0\\d{9}")) {
            throw new IllegalArgumentException(
                    "Patient mobile number must contain 10 digits and start with 0, for example 0412345678.");
        }
        return digits;
    }

    /**
     * Returns the name of this patient.
     *
     * @return the full name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the mobile number of this patient.
     *
     * @return the mobile number as 10 digits
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Returns the patient details in a readable form.
     *
     * @return the name and the mobile number of this patient
     */
    @Override
    public String toString() {
        return name + " (" + mobile + ")";
    }
}
