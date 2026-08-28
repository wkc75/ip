package zhangwei.ui;

import java.util.Scanner;

import zhangwei.task.Task;
import zhangwei.task.TaskList;

/**
 * Deals with everything the user sees and types: it reads commands from the
 * console and prints the chatbot's replies.
 *
 * <p>Collecting the console work here means the rest of the program never
 * calls {@code System.out} directly, so the wording of a message can be
 * changed, or the whole console replaced with a window, without touching the
 * classes that decide what to say.
 */
public class Ui {

    /**
     * The chatbot's name in ASCII art, shown once at startup.
     *
     * <p>Each backslash in the art must be written as \\ in a Java string
     * literal, because \ starts an escape sequence.
     */
    private static final String BANNER =
            " ______                     __        __   _ \n"
            + "|__  / |__   __ _ _ __   __ \\ \\      / /__(_)\n"
            + "  / /| '_ \\ / _` | '_ \\ / _` \\ \\ /\\ / / _ \\ |\n"
            + " / /_| | | | (_| | | | | (_| |\\ V  V /  __/ |\n"
            + "/____|_| |_|\\__,_|_| |_|\\__, | \\_/\\_/ \\___|_|\n"
            + "                        |___/\n";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the banner and the greeting shown when the chatbot starts. */
    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println("Hello! I'm ZhangWei.");
        System.out.println("What can I do for you?");
    }

    /** Returns the next line the user types, without the line separator. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the farewell shown just before the chatbot exits. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Prints a message the chatbot wants the user to read, such as a
     * complaint about the save file.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Prints something that went wrong, in words the user can act on.
     *
     * <p>Separate from {@link #showMessage} so that errors can later be
     * highlighted (a prefix, a colour, a different stream) in one place.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /** Confirms that a task was added, and reports the new task count. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        showTask(task);
        showTaskCount(taskCount);
    }

    /** Confirms that a task was removed, and reports the new task count. */
    public void showTaskRemoved(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        showTask(task);
        showTaskCount(taskCount);
    }

    /** Confirms that a task was marked as done. */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        showTask(task);
    }

    /** Confirms that a task was marked as not done. */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        showTask(task);
    }

    /** Prints every stored task, numbered from 1, with its done status. */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.println(i + "." + tasks.get(i));
        }
    }

    /** Prints the given task, indented, as it appears to the user. */
    private void showTask(Task task) {
        System.out.println("  " + task);
    }

    /** Reports how many tasks are stored, after adding or removing one. */
    private void showTaskCount(int taskCount) {
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Releases the console once the chatbot has finished with it. */
    public void close() {
        scanner.close();
    }
}
