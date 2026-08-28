package zhangwei.command;

import zhangwei.storage.Storage;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/** Shows every task currently in the list. */
public class ListCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
