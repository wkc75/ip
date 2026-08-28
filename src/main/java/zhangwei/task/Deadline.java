package zhangwei.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that must be done before a specific date,
 * e.g. "return book (by: Dec 2 2019)".
 */
public class Deadline extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /** The date this task must be done by. */
    private final LocalDate by;

    /**
     * Creates a deadline with the given description and due date.
     *
     * @param description what is to be done, e.g. "return book".
     * @param by the date the task must be done by.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due date this deadline was created with.
     *
     * @return the date this task is due by.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns this deadline as it appears to the user, with the date written
     * in a readable form rather than the ISO form it was typed in.
     *
     * @return this deadline as one line,
     *     e.g. "[D][ ] return book (by: Dec 2 2019)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + by.format(DISPLAY_FORMAT) + ")";
    }
}
