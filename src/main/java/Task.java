/**
 * A single task tracked by the chatbot: a description plus whether it is done.
 */
public class Task {

    protected String description;
    protected boolean isDone;
    protected String typeIcon;

    /**
     * Creates a task with the given description and type icon
     * (e.g. "T" for a todo). A newly created task is always not done.
     */
    public Task(String description, String typeIcon) {
        this.description = description;
        this.typeIcon = typeIcon;
        this.isDone = false;
    }

    /** Creates a todo: a task with no date or time attached. */
    public Task(String description) {
        this(description, "T");
    }

    /** Returns the single-letter icon for this task's type. */
    public String getTypeIcon() {
        return typeIcon;
    }

    /** Returns "X" if this task is done, or a single space otherwise. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the description this task was created with. */
    public String getDescription() {
        return description;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task as it should appear to the user,
     * e.g. "[T][X] read book".
     */
    @Override
    public String toString() {
        return "[" + typeIcon + "][" + getStatusIcon() + "] " + description;
    }
}
