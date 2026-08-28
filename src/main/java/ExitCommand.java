/** Says goodbye and ends the session. */
public class ExitCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Returns true: this is the one command that stops the chatbot. */
    @Override
    public boolean isExit() {
        return true;
    }
}
