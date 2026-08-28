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

    protected LocalDate by;

    /** Creates a deadline with the given description and due date. */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /** Returns the due date this deadline was created with. */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns this deadline as it appears to the user,
     * e.g. "[D][ ] return book (by: Dec 2 2019)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + by.format(DISPLAY_FORMAT) + ")";
    }
}
