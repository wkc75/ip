/**
 * A task tracked by the chatbot: a description plus whether it is done.
 * Subclasses add whatever extra detail their task type needs.
 */
public class Task {

    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     * A newly created task is always not done.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
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
     * Returns the status and description shared by every task type,
     * e.g. "[X] read book". Subclasses prepend their own type icon.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
