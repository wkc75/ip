package zhangwei.command;

import zhangwei.ZhangWeiException;
import zhangwei.storage.Storage;
import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/** Removes the task with the given number from the list. */
public class DeleteCommand extends Command {

    private final int taskNumber;

    /**
     * Creates a command that will delete the task with the given number.
     *
     * @param taskNumber the 1-based number of the task to delete, as the user
     *     sees it in the list.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the numbered task, reports it to the user, and saves the
     * shortened list.
     *
     * @param tasks the task list to delete from.
     * @param ui used to report the removal to the user.
     * @param storage used to save the updated list.
     * @throws ZhangWeiException if the number does not refer to a task, or if
     *     the updated list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZhangWeiException {
        // Whether the number refers to a task can only be judged against the
        // list, which does not exist yet when the command is parsed.
        tasks.requireTask(taskNumber);
        Task removed = tasks.delete(taskNumber);
        ui.showTaskRemoved(removed, tasks.size());
        storage.saveTasks(tasks);
    }
}
