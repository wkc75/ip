/**
 * A task that must be done before a specific date/time,
 * e.g. "return book (by: Sunday)".
 */
public class Deadline extends Task {

    protected String by;

    /** Creates a deadline with the given description and due date/time. */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /** Returns the due date/time this deadline was created with. */
    public String getBy() {
        return by;
    }

    /**
     * Returns this deadline as it appears to the user,
     * e.g. "[D][ ] return book (by: Sunday)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
