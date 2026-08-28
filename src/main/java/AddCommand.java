/**
 * Adds one already-built task to the list, whatever its type.
 *
 * <p>A todo, a deadline and an event differ only in how they are read from the
 * user's text, which the parser has already done by this point; adding them is
 * identical, so one command serves all three.
 */
public class AddCommand extends Command {

    private final Task task;

    /** Creates a command that will add the given task. */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZhangWeiException {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.saveTasks(tasks);
    }
}
