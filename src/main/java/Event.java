/**
 * A task that runs between a start and an end date/time,
 * e.g. "project meeting (from: Mon 2pm to: 4pm)".
 */
public class Event extends Task {

    protected String from;
    protected String to;

    /** Creates an event running from the given start to the given end. */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the start date/time of this event. */
    public String getFrom() {
        return from;
    }

    /** Returns the end date/time of this event. */
    public String getTo() {
        return to;
    }

    /**
     * Returns this event as it appears to the user,
     * e.g. "[E][ ] project meeting (from: Mon 2pm to: 4pm)".
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
