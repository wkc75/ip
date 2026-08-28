package zhangwei.command;

import zhangwei.ZhangWeiException;
import zhangwei.storage.Storage;
import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/** Marks the task with the given number as not done. */
public class UnmarkCommand extends Command {

    private final int taskNumber;

    /** Creates a command that will unmark the task with the given 1-based number. */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZhangWeiException {
        tasks.requireTask(taskNumber);
        Task task = tasks.get(taskNumber);
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
        storage.saveTasks(tasks);
    }
}
