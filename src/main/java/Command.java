/**
 * One instruction the user gave, ready to be carried out.
 *
 * <p>The parser turns a line of text into a {@code Command}, and the main loop
 * runs it without knowing which kind it is. Adding a new command therefore
 * means adding a subclass and one line in the parser, instead of another
 * branch in a switch that every existing command has to share.
 */
public abstract class Command {

    /**
     * Carries out this command.
     *
     * @param tasks the task list to read or change.
     * @param ui used to tell the user what happened.
     * @param storage used to keep the change on the hard disk.
     * @throws ZhangWeiException if the command cannot be carried out, with a
     *     message written for the user to read.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws ZhangWeiException;

    /**
     * Returns whether the chatbot should stop after this command.
     * Only the exit command overrides this.
     */
    public boolean isExit() {
        return false;
    }
}
