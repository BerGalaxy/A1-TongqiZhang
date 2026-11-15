import java.util.List;

/**
 * Represents a dietitian.
 * <p>
 * A dietitian prepares meal plans and works in a field of nutrition such as
 * clinical nutrition. The class adds the specialisation to the details that
 * every health professional already has.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class Dietitian extends HealthProfessional {

    /** Field of nutrition in which the dietitian specialises. */
    private String specialisation;

    /**
     * Creates a dietitian with default values.
     */
    public Dietitian() {
        super();
        this.specialisation = "General Nutrition";
    }

    /**
     * Creates a dietitian with the supplied details.
     *
     * @param id             unique identifier; digits only
     * @param name           full name; must not be blank
     * @param availableDays  working days; must not be empty
     * @param specialisation field of nutrition; must not be blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, List<String> availableDays, String specialisation) {
        super(id, name, availableDays);
        this.specialisation = validateSpecialisation(specialisation);
    }

    /**
     * Returns the field of nutrition in which this dietitian specialises.
     *
     * @return the specialisation
     */
    public String getSpecialisation() {
        return specialisation;
    }

    /**
     * Changes the specialisation of this dietitian.
     *
     * @param specialisation new specialisation; must not be blank
     * @throws IllegalArgumentException if the specialisation is null or blank
     */
    public void setSpecialisation(String specialisation) {
        this.specialisation = validateSpecialisation(specialisation);
    }

    /**
     * Checks a specialisation and returns it in a clean form.
     *
     * @param specialisation the specialisation to check
     * @return the specialisation without surrounding spaces
     * @throws IllegalArgumentException if the specialisation is null or blank
     */
    private static String validateSpecialisation(String specialisation) {
        if (specialisation == null || specialisation.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialisation must not be empty.");
        }
        return specialisation.trim();
    }

    /**
     * Returns the type of this health professional.
     *
     * @return the text "Dietitian"
     */
    @Override
    public String getProfessionalType() {
        return "Dietitian";
    }

    /**
     * Creates a meal plan for a patient.
     *
     * @param patientName name of the patient
     * @return a description of the duty that was performed
     */
    @Override
    public String performCoreDuty(String patientName) {
        return getName() + " creates a " + specialisation + " meal plan for " + patientName + ".";
    }

    /**
     * Returns the shared details of the professional followed by the
     * specialisation.
     *
     * @return the details of this dietitian
     */
    @Override
    public String toString() {
        return super.toString() + "\nSpecialisation    : " + specialisation;
    }
}
