import java.util.ArrayList;
import java.util.Scanner;

/**
 * ZhangWei
 * This is a chatbot named ZhangWei
 */
public class ZhangWei {

    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static final Storage storage = new Storage("./data/zhangwei.txt");

    /**
     * Stores the given task, confirms it,
     * and reports how many tasks are now stored.
     */
    private static void addTask(Task task) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        printTask(task);
        reportCount();
        save();
    }

    /**
     * Removes the task with the given 1-based number and confirms it.
     * Assumes the number refers to an existing task.
     */
    private static void deleteTask(int taskNumber) {
        Task removed = tasks.remove(taskNumber - 1);
        System.out.println("Noted. I've removed this task:");
        printTask(removed);
        reportCount();
        save();
    }

    /** Reports how many tasks are stored, after adding or removing one. */
    private static void reportCount() {
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Prints every stored task, numbered from 1, with its done status. */
    private static void listTasks() {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Prints the given task, indented, as it appears to the user. */
    private static void printTask(Task task) {
        System.out.println("  " + task);
    }

    /**
     * Returns the 0-based index of the task the given argument refers to.
     *
     * @param arguments the text typed after the command word, e.g. "2".
     * @param command the command asking, used to phrase the examples.
     * @throws ZhangWeiException if it is missing, not a number,
     *     or does not refer to an existing task.
     */
    private static int parseTaskIndex(String arguments, Command command)
            throws ZhangWeiException {
        String example = "For example: " + command.getKeyword() + " 2";
        if (arguments.isEmpty()) {
            throw new ZhangWeiException("Which task? " + example);
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new ZhangWeiException("\"" + arguments + "\" is not a task number. "
                    + example);
        }

        if (tasks.isEmpty()) {
            throw new ZhangWeiException("You have no tasks yet, so there is no task "
                    + taskNumber + ".");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ZhangWeiException("There is no task " + taskNumber + ". "
                    + "You have " + tasks.size() + " tasks.");
        }
        return taskNumber - 1;
    }

    /**
     * Marks the task with the given 1-based number as done and confirms it.
     * Assumes the number refers to an existing task.
     */
    private static void markTask(int taskNumber) {
        int index = taskNumber - 1;
        tasks.get(index).markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        printTask(tasks.get(index));
        save();
    }

    /**
     * Marks the task with the given 1-based number as not done and confirms it.
     * Assumes the number refers to an existing task.
     */
    private static void unmarkTask(int taskNumber) {
        int index = taskNumber - 1;
        tasks.get(index).markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        printTask(tasks.get(index));
        save();
    }

    /**
     * Returns a todo built from the text typed after "todo".
     *
     * @throws ZhangWeiException if no description was given.
     */
    private static Todo parseTodo(String arguments) throws ZhangWeiException {
        if (arguments.isEmpty()) {
            throw new ZhangWeiException("A todo needs a description. "
                    + "For example: todo read book");
        }
        rejectSeparator(arguments);
        return new Todo(arguments);
    }

    /**
     * Returns a deadline built from text of the form
     * "description /by date". 
     *
     * @throws ZhangWeiException if the description or the /by part is missing.
     */
    private static Deadline parseDeadline(String arguments) throws ZhangWeiException {
        String[] parts = arguments.split("/by", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new ZhangWeiException("A deadline needs a description and a /by. "
                    + "For example: deadline return book /by Sunday");
        }
        rejectSeparator(arguments);
        return new Deadline(parts[0].trim(), parts[1].trim());
    }

    /**
     * Returns an event built from text of the form
     * "description /from start /to end".
     *
     * @throws ZhangWeiException if the description, the /from or the /to is missing.
     */
    private static Event parseEvent(String arguments) throws ZhangWeiException {
        String[] parts = arguments.split("/from|/to");
        if (parts.length < 3 || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
            throw new ZhangWeiException("An event needs a description, a /from and a /to. "
                    + "For example: event project meeting /from Mon 2pm /to 4pm");
        }
        rejectSeparator(arguments);
        return new Event(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    /**
     * Rejects text containing the character that separates fields in the save
     * file. Allowing it would split one task across several fields, so the task
     * would come back wrong (or not at all) the next time the chatbot starts.
     *
     * @throws ZhangWeiException if the text contains "|".
     */
    private static void rejectSeparator(String text) throws ZhangWeiException {
        if (text.contains("|")) {
            throw new ZhangWeiException("A task cannot contain \"|\", because "
                    + "that character separates the fields in the save file.");
        }
    }

    /**
     * Saves the current task list, reporting a failure to the user instead of
     * ending the session. A chatbot that still works but cannot save is far
     * more useful than one that stops at the first disk problem.
     */
    private static void save() {
        try {
            storage.saveTasks(tasks);
        } catch (ZhangWeiException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Fills the task list from the save file.
     *
     * @return a message to show the user if anything was wrong with the save
     *     file, or null if the load was clean.
     */
    private static String loadSavedTasks() {
        try {
            Storage.LoadResult loaded = storage.loadTasks();
            tasks.addAll(loaded.tasks());
            if (loaded.skippedLines() == 0) {
                return null;
            }

            String message = "I could not understand " + loaded.skippedLines()
                    + " line(s) in your save file, so I skipped them.";
            if (loaded.backupPath() != null) {
                message += " The original file is kept at " + loaded.backupPath() + ".";
            }
            return message;
        } catch (ZhangWeiException e) {
            // The file exists but is unreadable. Starting empty keeps the
            // chatbot usable; the file is left untouched so it can be repaired.
            return e.getMessage() + " Starting with an empty task list.";
        }
    }

    public static void main(String[] args) {
        // Tasks are loaded before anything is printed, but any complaint
        // about the save file is shown after the greeting, where the user
        // is actually looking.
        String loadMessage = loadSavedTasks();

        // Each backslash in the ASCII art must be written as \\ in a Java
        // string literal, because \ starts an escape sequence.
        String banner = " ______                     __        __   _ \n"
                + "|__  / |__   __ _ _ __   __ \\ \\      / /__(_)\n"
                + "  / /| '_ \\ / _` | '_ \\ / _` \\ \\ /\\ / / _ \\ |\n"
                + " / /_| | | | (_| | | | | (_| |\\ V  V /  __/ |\n"
                + "/____|_| |_|\\__,_|_| |_|\\__, | \\_/\\_/ \\___|_|\n"
                + "                        |___/\n";
        System.out.println(banner);

        System.out.println("Hello! I'm ZhangWei.");
        System.out.println("What can I do for you?");
        if (loadMessage != null) {
            System.out.println(loadMessage);
        }

        Scanner scan = new Scanner(System.in);

        boolean isRunning = true;

        while (isRunning) {
            String input = scan.nextLine();
            if (input.isBlank()) {
                continue;
            }

            // The first word is the command keyword; the rest is its argument.
            String keyword = input.split(" ")[0];
            // Everything after the keyword, e.g. "return book /by Sunday".
            String arguments = input.substring(keyword.length()).trim();

            // Anything the chatbot can explain to the user arrives here as a
            // ZhangWeiException, so one handler reports them all.
            try {
                Command command = Command.fromKeyword(keyword);
                switch (command) {
                case BYE -> {
                    isRunning = false;
                    System.out.println("Bye. Hope to see you again soon!");
                }
                case LIST -> listTasks();
                case MARK -> markTask(parseTaskIndex(arguments, command) + 1);
                case UNMARK -> unmarkTask(parseTaskIndex(arguments, command) + 1);
                case DELETE -> deleteTask(parseTaskIndex(arguments, command) + 1);
                case TODO -> addTask(parseTodo(arguments));
                case DEADLINE -> addTask(parseDeadline(arguments));
                case EVENT -> addTask(parseEvent(arguments));
                }
            } catch (ZhangWeiException e) {
                System.out.println(e.getMessage());
            }
        }
        scan.close();
    }
}
