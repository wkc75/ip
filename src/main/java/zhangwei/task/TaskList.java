package zhangwei.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zhangwei.ZhangWeiException;

/**
 * The list of tasks the chatbot is keeping, together with the operations that
 * change it.
 *
 * <p>Task numbers are 1-based everywhere the user sees them and 0-based inside
 * this class. Translating between the two, and refusing numbers that do not
 * refer to a task, is this class's job: the list knows how many tasks it holds,
 * so no other class has to guard the bounds itself.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list holding the given tasks, e.g. the ones just loaded. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds the given task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task with the given 1-based number.
     * The number must already have been checked by {@link #requireTask}.
     */
    public Task delete(int taskNumber) {
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the task with the given 1-based number.
     * The number must already have been checked by {@link #requireTask}.
     */
    public Task get(int taskNumber) {
        return tasks.get(taskNumber - 1);
    }

    /** Returns how many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether the list holds no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks in order, for reading only. Callers cannot change the
     * list through it, so every change still goes through this class.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Checks that the given 1-based number refers to a task in this list.
     *
     * @throws ZhangWeiException if the list is empty or the number is out of
     *     range, explaining which numbers would have worked.
     */
    public void requireTask(int taskNumber) throws ZhangWeiException {
        if (tasks.isEmpty()) {
            throw new ZhangWeiException("You have no tasks yet, so there is no task "
                    + taskNumber + ".");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ZhangWeiException("There is no task " + taskNumber + ". "
                    + "You have " + tasks.size() + " tasks.");
        }
    }
}
