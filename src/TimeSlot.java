import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents one fixed appointment slot in the clinic's daily schedule.
 * <p>
 * The clinic opens at {@link #CLINIC_OPENING_HOUR}:00 and closes at
 * {@link #CLINIC_CLOSING_HOUR}:00. Appointments start every
 * {@link #SLOT_LENGTH_MINUTES} minutes. The schedule is built from these
 * constants, so changing a constant changes the schedule for the whole system
 * without changing any other class.
 * </p>
 *
 * @author Tongqi Zhang
 */
public class TimeSlot implements Comparable<TimeSlot> {

    /** Hour at which the clinic opens. */
    public static final int CLINIC_OPENING_HOUR = 8;

    /** Hour at which the clinic closes. */
    public static final int CLINIC_CLOSING_HOUR = 16;

    /** Length of one appointment slot in minutes. */
    public static final int SLOT_LENGTH_MINUTES = 30;

    /** First slot in the schedule, expressed in minutes from midnight. */
    private static final int CLINIC_OPENING_MINUTE = CLINIC_OPENING_HOUR * 60;

    /** Closing time, expressed in minutes from midnight. */
    private static final int CLINIC_CLOSING_MINUTE = CLINIC_CLOSING_HOUR * 60;

    /** All slots of a normal clinic day, in time order. */
    private static final List<TimeSlot> DAILY_SCHEDULE = buildDailySchedule();

    /** Minutes from midnight at which this slot starts. */
    private final int startMinuteOfDay;

    /**
     * Creates a slot for internal use; use {@link #parse(String)} to create a
     * valid slot from text.
     *
     * @param startMinuteOfDay minutes from midnight at which the slot starts
     */
    private TimeSlot(int startMinuteOfDay) {
        this.startMinuteOfDay = startMinuteOfDay;
    }

    /**
     * Builds the fixed daily schedule from the clinic constants.
     *
     * @return an unmodifiable list of all slots in the day
     */
    private static List<TimeSlot> buildDailySchedule() {
        List<TimeSlot> slots = new ArrayList<>();
        // Walking from the opening time to the closing time keeps the schedule
        // flexible: editing the constants above is enough to change it.
        for (int minute = CLINIC_OPENING_MINUTE; minute < CLINIC_CLOSING_MINUTE; minute += SLOT_LENGTH_MINUTES) {
            slots.add(new TimeSlot(minute));
        }
        return Collections.unmodifiableList(slots);
    }

    /**
     * Creates a time slot from text such as "09:30".
     * <p>
     * The text must use the 24-hour clock, must start on a 30-minute boundary
     * and must fall inside the clinic's opening hours.
     * </p>
     *
     * @param text the time to parse, for example "09:30"
     * @return the matching time slot
     * @throws IllegalArgumentException if the text is not a valid clinic slot
     */
    public static TimeSlot parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment time must not be empty.");
        }

        String value = text.trim();
        if (!value.matches("\\d{1,2}:\\d{2}")) {
            throw new IllegalArgumentException(
                    "Appointment time must be written as HH:mm, for example 09:30.");
        }

        int colonIndex = value.indexOf(':');
        int hour = Integer.parseInt(value.substring(0, colonIndex));
        int minute = Integer.parseInt(value.substring(colonIndex + 1));

        // Reject impossible times such as 09:60 before the range check below.
        if (hour > 23 || minute > 59) {
            throw new IllegalArgumentException(
                    "Appointment time must be a valid 24-hour time, for example 09:30.");
        }

        if (minute % SLOT_LENGTH_MINUTES != 0) {
            throw new IllegalArgumentException("Appointment time must start on a "
                    + SLOT_LENGTH_MINUTES + "-minute slot, for example 09:00 or 09:30.");
        }

        int startMinute = hour * 60 + minute;
        if (startMinute < CLINIC_OPENING_MINUTE || startMinute >= CLINIC_CLOSING_MINUTE) {
            throw new IllegalArgumentException("Appointment time must be between "
                    + formatTime(CLINIC_OPENING_MINUTE) + " and " + formatTime(CLINIC_CLOSING_MINUTE)
                    + " (30-minute slots).");
        }

        return new TimeSlot(startMinute);
    }

    /**
     * Returns every slot of a normal clinic day.
     *
     * @return an unmodifiable list of the daily schedule in time order
     */
    public static List<TimeSlot> getDailySchedule() {
        return DAILY_SCHEDULE;
    }

    /**
     * Formats minutes from midnight as HH:mm.
     *
     * @param minuteOfDay minutes from midnight
     * @return the time as text such as "09:30"
     */
    private static String formatTime(int minuteOfDay) {
        return String.format("%02d:%02d", minuteOfDay / 60, minuteOfDay % 60);
    }

    /**
     * Returns the start time of this slot.
     *
     * @return the start time as text such as "09:30"
     */
    public String getStartTime() {
        return formatTime(startMinuteOfDay);
    }

    /**
     * Returns the end time of this slot.
     *
     * @return the end time as text such as "10:00"
     */
    public String getEndTime() {
        return formatTime(startMinuteOfDay + SLOT_LENGTH_MINUTES);
    }

    /**
     * Returns the whole slot as a readable range.
     *
     * @return the time range such as "09:30 - 10:00"
     */
    public String getTimeRange() {
        return getStartTime() + " - " + getEndTime();
    }

    /**
     * Returns the start time of this slot.
     *
     * @return the start time as text such as "09:30"
     */
    @Override
    public String toString() {
        return getStartTime();
    }

    /**
     * Compares this slot with another slot by start time.
     *
     * @param other the slot to compare with
     * @return a negative number, zero or a positive number if this slot is
     *         earlier than, equal to or later than the other slot
     */
    @Override
    public int compareTo(TimeSlot other) {
        return Integer.compare(startMinuteOfDay, other.startMinuteOfDay);
    }

    /**
     * Compares this slot with another object.
     *
     * @param other the object to compare with
     * @return {@code true} if the other object is a slot with the same start time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TimeSlot)) {
            return false;
        }
        TimeSlot that = (TimeSlot) other;
        return startMinuteOfDay == that.startMinuteOfDay;
    }

    /**
     * Returns a hash code that is consistent with {@link #equals(Object)}.
     *
     * @return the hash code of the start time
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(startMinuteOfDay);
    }
}
