import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a health professional who works for the clinic.
 * <p>
 * This class stores the information that every health professional shares: an
 * identifier that contains digits only, a name, and the days of the week on
 * which the professional is available. The class is abstract because a
 * "generic" health professional has no specialty, so only the concrete
 * subclasses can be instantiated.
 * </p>
 *
 * @author Tongqi Zhang
 */
public abstract class HealthProfessional {

    /** Identifier used when the caller does not supply one. */
    public static final String DEFAULT_ID = "0000";

    /** Name used when the caller does not supply one. */
    public static final String DEFAULT_NAME = "Unnamed Health Professional";

    /** Weekday names accepted by {@link #setAvailableDays(List)}. */
    private static final List<String> VALID_DAYS = Collections.unmodifiableList(
            Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));

    /** Unique numeric identifier of the professional. */
    private String id;

    /** Full name of the professional. */
    private String name;

    /** Days of the week on which the professional works. */
    private List<String> availableDays;

    /**
     * Creates a health professional with sensible default values.
     * <p>
     * Subclasses use this constructor when the caller does not supply any
     * details, for example when a default professional has to be created.
     * </p>
     */
    protected HealthProfessional() {
        this(DEFAULT_ID, DEFAULT_NAME, Arrays.asList("Monday", "Wednesday", "Friday"));
    }

    /**
     * Creates a health professional with the supplied details.
     *
     * @param id            unique identifier; digits only
     * @param name          full name; must not be blank
     * @param availableDays working days; must not be empty and must use valid weekday names
     * @throws IllegalArgumentException if any argument is invalid
     */
    protected HealthProfessional(String id, String name, List<String> availableDays) {
        this.id = validateId(id);
        this.name = validateName(name);
        this.availableDays = validateAvailableDays(availableDays);
    }

    /**
     * Returns the type of health professional, for example "Nurse".
     *
     * @return the type name that is shown when the object is printed
     */
    public abstract String getProfessionalType();

    /**
     * Performs the duty that is specific to this type of professional.
     *
     * @param patientName name of the patient who receives the service
     * @return a description of the duty that was performed
     */
    public abstract String performCoreDuty(String patientName);

    /**
     * Returns the identifier of this professional.
     *
     * @return the numeric identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Changes the identifier of this professional.
     * <p>
     * The identifier is checked before it is stored so that an object can never
     * hold an invalid identifier.
     * </p>
     *
     * @param id new identifier; digits only
     * @throws IllegalArgumentException if the identifier is null, blank or contains non-digits
     */
    public void setId(String id) {
        this.id = validateId(id);
    }

    /**
     * Checks an identifier and returns it in a clean form.
     *
     * @param id the identifier to check
     * @return the identifier without surrounding spaces
     * @throws IllegalArgumentException if the identifier is null, blank or contains non-digits
     */
    private static String validateId(String id) {
        if (id == null || !id.trim().matches("\\d+")) {
            throw new IllegalArgumentException("Health professional ID must contain digits only.");
        }
        return id.trim();
    }

    /**
     * Returns the name of this professional.
     *
     * @return the full name
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name of this professional.
     *
     * @param name new name; must not be blank
     * @throws IllegalArgumentException if the name is null or blank
     */
    public void setName(String name) {
        this.name = validateName(name);
    }

    /**
     * Checks a name and returns it in a clean form.
     *
     * @param name the name to check
     * @return the name without surrounding spaces
     * @throws IllegalArgumentException if the name is null or blank
     */
    private static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Health professional name must not be empty.");
        }
        return name.trim();
    }

    /**
     * Returns the days on which this professional is available.
     * <p>
     * The returned list cannot be modified, so the internal state of the object
     * stays valid.
     * </p>
     *
     * @return an unmodifiable list of weekday names
     */
    public List<String> getAvailableDays() {
        return availableDays;
    }

    /**
     * Changes the days on which this professional is available.
     *
     * @param availableDays new working days; must not be empty
     * @throws IllegalArgumentException if the list is null, empty or contains an unknown day
     */
    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = validateAvailableDays(availableDays);
    }

    /**
     * Checks the working days and returns them in a clean, unmodifiable form.
     *
     * @param availableDays the days to check
     * @return an unmodifiable list of valid weekday names
     * @throws IllegalArgumentException if the list is null, empty or contains an unknown day
     */
    private static List<String> validateAvailableDays(List<String> availableDays) {
        if (availableDays == null || availableDays.isEmpty()) {
            throw new IllegalArgumentException("A health professional must have at least one available day.");
        }

        List<String> checkedDays = new ArrayList<>();
        for (String day : availableDays) {
            if (day == null || !VALID_DAYS.contains(day)) {
                throw new IllegalArgumentException(
                        "Invalid available day: " + day + ". Use a full weekday name such as Monday.");
            }
            if (!checkedDays.contains(day)) {
                checkedDays.add(day);
            }
        }
        return Collections.unmodifiableList(checkedDays);
    }

    /**
     * Returns the available days as one readable line, for example
     * "Monday, Wednesday, Friday".
     *
     * @return the available days separated by commas
     */
    public String getAvailableDaysAsText() {
        return String.join(", ", availableDays);
    }

    /**
     * Returns a one-line summary that is used inside appointment details.
     *
     * @return the name, identifier and type of this professional
     */
    public String getSummary() {
        return name + " (ID " + id + ", " + getProfessionalType() + ")";
    }

    /**
     * Returns all details of this professional in a readable form.
     *
     * @return the details of this professional
     */
    @Override
    public String toString() {
        return "Type              : " + getProfessionalType() + "\n"
                + "ID                : " + id + "\n"
                + "Name              : " + name + "\n"
                + "Available days    : " + getAvailableDaysAsText();
    }

    /**
     * Compares this professional with another object.
     * <p>
     * Two health professionals are equal when they have the same identifier,
     * regardless of their concrete type. Appointment conflict checks rely on
     * this rule.
     * </p>
     *
     * @param other the object to compare with
     * @return {@code true} if the other object is a health professional with the same ID
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) {
            return false;
        }
        HealthProfessional that = (HealthProfessional) other;
        return id.equals(that.id);
    }

    /**
     * Returns a hash code that is consistent with {@link #equals(Object)}.
     *
     * @return the hash code of the identifier
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
