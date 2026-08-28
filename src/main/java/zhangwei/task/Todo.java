package zhangwei.task;

/**
 * A task with no date or time attached to it, e.g. "borrow book".
 */
public class Todo extends Task {

    /** Creates a todo with the given description. */
    public Todo(String description) {
        super(description);
    }

    /** Returns this todo as it appears to the user, e.g. "[T][ ] borrow book". */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
