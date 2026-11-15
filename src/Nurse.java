import java.util.List;

/**
 * Represents a nurse.
 * <p>
 * A nurse works in a hospital department and can take blood samples. The class
 * adds the department to the details that every health professional already
 * has.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class Nurse extends HealthProfessional {

    /** Department in which the nurse works. */
    private String department;

    /**
     * Creates a nurse with default values.
     */
    public Nurse() {
        super();
        this.department = "General Ward";
    }

    /**
     * Creates a nurse with the supplied details.
     *
     * @param id            unique identifier; digits only
     * @param name          full name; must not be blank
     * @param availableDays working days; must not be empty
     * @param department    department in which the nurse works; must not be blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(String id, String name, List<String> availableDays, String department) {
        super(id, name, availableDays);
        this.department = validateDepartment(department);
    }

    /**
     * Returns the department in which this nurse works.
     *
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Changes the department of this nurse.
     *
     * @param department new department; must not be blank
     * @throws IllegalArgumentException if the department is null or blank
     */
    public void setDepartment(String department) {
        this.department = validateDepartment(department);
    }

    /**
     * Checks a department and returns it in a clean form.
     *
     * @param department the department to check
     * @return the department without surrounding spaces
     * @throws IllegalArgumentException if the department is null or blank
     */
    private static String validateDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department must not be empty.");
        }
        return department.trim();
    }

    /**
     * Returns the type of this health professional.
     *
     * @return the text "Nurse"
     */
    @Override
    public String getProfessionalType() {
        return "Nurse";
    }

    /**
     * Takes a blood sample from a patient.
     *
     * @param patientName name of the patient
     * @return a description of the duty that was performed
     */
    @Override
    public String performCoreDuty(String patientName) {
        return getName() + " takes a blood sample from " + patientName + ".";
    }

    /**
     * Returns the shared details of the professional followed by the department.
     *
     * @return the details of this nurse
     */
    @Override
    public String toString() {
        return super.toString() + "\nDepartment        : " + department;
    }
}
