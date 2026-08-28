/** Marks the task with the given number as done. */
public class MarkCommand extends Command {

    private final int taskNumber;

    /** Creates a command that will mark the task with the given 1-based number. */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZhangWeiException {
        tasks.requireTask(taskNumber);
        Task task = tasks.get(taskNumber);
        task.markAsDone();
        ui.showTaskMarked(task);
        storage.saveTasks(tasks);
    }
}
