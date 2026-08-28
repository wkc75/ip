package zhangwei.command;

import zhangwei.ZhangWeiException;
import zhangwei.storage.Storage;
import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/** Marks the task with the given number as done. */
public class MarkCommand extends Command {

    private final int taskNumber;

    /**
     * Creates a command that will mark the task with the given number as done.
     *
     * @param taskNumber the 1-based number of the task to mark, as the user
     *     sees it in the list.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the numbered task as done, shows it to the user, and saves the
     * updated list.
     *
     * @param tasks the task list holding the task to mark.
     * @param ui used to show the newly marked task.
     * @param storage used to save the updated list.
     * @throws ZhangWeiException if the number does not refer to a task, or if
     *     the updated list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZhangWeiException {
        tasks.requireTask(taskNumber);
        Task task = tasks.get(taskNumber);
        task.markAsDone();
        ui.showTaskMarked(task);
        storage.saveTasks(tasks);
    }
}
