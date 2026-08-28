package zhangwei.task;

/**
 * A task tracked by the chatbot: a description plus whether it is done.
 * Subclasses add whatever extra detail their task type needs.
 */
public class Task {

    /** What the user wants to be reminded of, e.g. "read book". */
    protected String description;

    /** Whether the user has marked this task as done. */
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     * A newly created task is always not done.
     *
     * @param description what the user wants to be reminded of, e.g. "read book".
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon shown inside the status brackets.
     *
     * @return "X" if this task is done, or a single space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description this task was created with.
     *
     * @return the task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
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
     *
     * @return this task's status and description as one line.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
