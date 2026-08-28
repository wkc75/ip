/** Removes the task with the given number from the list. */
public class DeleteCommand extends Command {

    private final int taskNumber;

    /** Creates a command that will delete the task with the given 1-based number. */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

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
