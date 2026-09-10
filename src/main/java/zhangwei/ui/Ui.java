package zhangwei.ui;

import java.util.List;
import java.util.Scanner;

import zhangwei.task.SortCriterion;
import zhangwei.task.Task;
import zhangwei.task.TaskList;

/**
 * Deals with everything the user sees and types: it reads commands from the
 * console and prints the chatbot's replies.
 *
 * <p>
 * Collecting the console work here means the rest of the program never
 * calls {@code System.out} directly, so the wording of a message can be
 * changed, or the whole console replaced with a window, without touching the
 * classes that decide what to say.
 *
 * <p>
 * Every line is written twice: once to the console, and once to an internal
 * buffer that {@link #drainOutput()} hands over. The console version of the
 * chatbot ignores the buffer, while the graphical version reads it instead of
 * the console, so both front ends get the same words from the same code.
 */
public class Ui {

    /**
     * The chatbot's name in ASCII art, shown once at startup.
     *
     * <p>
     * Each backslash in the art must be written as \\ in a Java string
     * literal, because \ starts an escape sequence.
     */
    private static final String BANNER = " ______                     __        __   _ \n"
            + "|__  / |__   __ _ _ __   __ \\ \\      / /__(_)\n"
            + "  / /| '_ \\ / _` | '_ \\ / _` \\ \\ /\\ / / _ \\ |\n"
            + " / /_| | | | (_| | | | | (_| |\\ V  V /  __/ |\n"
            + "/____|_| |_|\\__,_|_| |_|\\__, | \\_/\\_/ \\___|_|\n"
            + "                        |___/\n";

    private final Scanner scanner = new Scanner(System.in);

    /** Holds a copy of everything said since the last {@link #drainOutput()}. */
    private final StringBuilder buffer = new StringBuilder();

    /** Creates a Ui that reads from and writes to the console. */
    public Ui() {
    }

    /** Prints the banner and the greeting shown when the chatbot starts. */
    public void showWelcome() {
        say(BANNER);
        showGreeting();
    }

    /**
     * Prints the greeting alone, without the banner.
     *
     * <p>
     * The banner is drawn with spaces and slashes, so it only lines up in a
     * fixed-width console; a window shows this shorter greeting instead.
     */
    public void showGreeting() {
        say("Hello! I'm ZhangWei.");
        say("What can I do for you?");
    }

    /**
     * Reads one line of input from the console.
     *
     * @return the next line the user types, without the line separator.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the farewell shown just before the chatbot exits. */
    public void showGoodbye() {
        say("Bye. Hope to see you again soon!");
    }

    /**
     * Prints a message the chatbot wants the user to read, such as a
     * complaint about the save file.
     *
     * @param message the text to show.
     */
    public void showMessage(String message) {
        say(message);
    }

    /**
     * Prints something that went wrong, in words the user can act on.
     *
     * <p>
     * Separate from {@link #showMessage} so that errors can later be
     * highlighted (a prefix, a colour, a different stream) in one place.
     *
     * @param message the explanation of what went wrong.
     */
    public void showError(String message) {
        say(message);
    }

    /**
     * Confirms that a task was added, and reports the new task count.
     *
     * @param task      the task that was just added.
     * @param taskCount how many tasks the list holds now.
     */
    public void showTaskAdded(Task task, int taskCount) {
        say("Got it. I've added this task:");
        showTask(task);
        showTaskCount(taskCount);
    }

    /**
     * Confirms that a task was removed, and reports the new task count.
     *
     * @param task      the task that was just removed.
     * @param taskCount how many tasks the list holds now.
     */
    public void showTaskRemoved(Task task, int taskCount) {
        say("Noted. I've removed this task:");
        showTask(task);
        showTaskCount(taskCount);
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task that was just marked.
     */
    public void showTaskMarked(Task task) {
        say("Nice! I've marked this task as done:");
        showTask(task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task that was just unmarked.
     */
    public void showTaskUnmarked(Task task) {
        say("OK, I've marked this task as not done yet:");
        showTask(task);
    }

    /**
     * Prints every stored task, numbered from 1, with its done status.
     *
     * @param tasks the task list to show.
     */
    public void showTaskList(TaskList tasks) {
        say("Here are the tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            say(i + "." + tasks.get(i));
        }
    }

    /**
     * Confirms the order the list was just put into, then shows it, so the
     * user can read off the numbers the next command will use.
     *
     * @param criterion the order the list was sorted into.
     * @param tasks the task list, already sorted.
     */
    public void showTasksSorted(SortCriterion criterion, TaskList tasks) {
        say("Sorted your tasks by " + criterion.getKeyword() + ".");
        showTaskList(tasks);
    }

    /**
     * Prints the tasks that matched a search, numbered from 1 among the
     * matches rather than by their position in the full list.
     *
     * <p>
     * An empty result gets its own sentence instead of a heading with
     * nothing under it, which would read as though something went wrong.
     *
     * @param matches the matching tasks, in the order they appear in the list.
     */
    public void showMatchingTasks(List<Task> matches) {
        if (matches.isEmpty()) {
            say("There are no matching tasks in your list.");
            return;
        }

        say("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            say((i + 1) + "." + matches.get(i));
        }
    }

    /**
     * Returns everything said since this method was last called, and forgets
     * it, so that the next caller only sees the reply to the next command.
     *
     * <p>
     * The console front end has already printed this text and throws the
     * copy away; the graphical front end shows the copy in a dialog bubble.
     *
     * @return the buffered text, with the trailing line separator removed.
     */
    public String drainOutput() {
        String output = buffer.toString().strip();
        clearOutput();
        assert buffer.isEmpty()
                : "Draining must empty the buffer, or each reply would repeat the ones before it.";
        return output;
    }

    /**
     * Forgets everything said since the last drain, without handing it over.
     *
     * <p>
     * The console front end has already put those words on the screen, so
     * it has nothing to do with the copy kept for the graphical one.
     */
    public void clearOutput() {
        buffer.setLength(0);
    }

    /**
     * Prints the given task, indented, as it appears to the user.
     *
     * @param task the task to show.
     */
    private void showTask(Task task) {
        say("  " + task);
    }

    /**
     * Reports how many tasks are stored, after adding or removing one.
     *
     * @param taskCount how many tasks the list holds now.
     */
    private void showTaskCount(int taskCount) {
        say("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Says one line, to the console and to the buffer.
     *
     * <p>
     * Every message in this class goes through here, so a front end that
     * cannot use the console only has to read the buffer.
     *
     * @param message the line to say.
     */
    private void say(String message) {
        System.out.println(message);
        buffer.append(message).append(System.lineSeparator());
    }

    /** Releases the console once the chatbot has finished with it. */
    public void close() {
        scanner.close();
    }
}
