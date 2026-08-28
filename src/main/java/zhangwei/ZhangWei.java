package zhangwei;

import zhangwei.command.Command;
import zhangwei.parser.Parser;
import zhangwei.storage.Storage;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/**
 * Entry point of ZhangWei, a chatbot that keeps track of the user's tasks.
 *
 * <p>This class only wires the other classes together and runs the command
 * loop: the {@link Ui} talks to the user, the {@link Parser} makes sense of
 * what was typed, each {@link Command} carries one instruction out, the
 * {@link TaskList} holds the tasks and the {@link Storage} keeps them on disk.
 */
public class ZhangWei {

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * A complaint about the save file, or null if it loaded cleanly. It is
     * held rather than printed because loading happens before the greeting,
     * and the message belongs after it, where the user is looking.
     */
    private final String loadMessage;

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * <p>A save file that cannot be read is not fatal: the chatbot starts with
     * an empty list and says so, leaving the file untouched so it can be
     * repaired, because a chatbot that still works is more useful than one
     * that refuses to start.
     *
     * @param filePath path of the text file the tasks are loaded from and
     *     saved to, e.g. "./data/zhangwei.txt".
     */
    public ZhangWei(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        String message = null;
        TaskList loadedTasks;
        try {
            Storage.LoadResult loaded = storage.loadTasks();
            loadedTasks = new TaskList(loaded.tasks());
            if (loaded.skippedLines() > 0) {
                message = "I could not understand " + loaded.skippedLines()
                        + " line(s) in your save file, so I skipped them.";
                if (loaded.backupPath() != null) {
                    message += " The original file is kept at "
                            + loaded.backupPath() + ".";
                }
            }
        } catch (ZhangWeiException e) {
            loadedTasks = new TaskList();
            message = e.getMessage() + " Starting with an empty task list.";
        }
        this.tasks = loadedTasks;
        this.loadMessage = message;
    }

    /** Greets the user, then runs commands until one of them ends the session. */
    public void run() {
        ui.showWelcome();
        if (loadMessage != null) {
            ui.showMessage(loadMessage);
        }

        boolean isExit = false;
        while (!isExit) {
            // Anything the chatbot can explain to the user arrives here as a
            // ZhangWeiException, so one handler reports them all.
            try {
                String fullCommand = ui.readCommand();
                // A blank line asks for nothing, so nothing is said about it.
                if (fullCommand.isBlank()) {
                    continue;
                }
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (ZhangWeiException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    /**
     * Starts the chatbot with its default save file.
     *
     * @param args command line arguments, which this chatbot does not use.
     */
    public static void main(String[] args) {
        new ZhangWei("./data/zhangwei.txt").run();
    }
}
