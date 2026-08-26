import java.util.Scanner;

/**
 * ZhangWei
 * This is a chatbot named ZhangWei
 */
public class ZhangWei {

    private static final int MAX_TASKS = 100;
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    /**
     * Stores the given task, confirms it,
     * and reports how many tasks are now stored.
     *
     * @throws ZhangWeiException if the list is already full.
     */
    private static void addTask(Task task) throws ZhangWeiException {
        if (taskCount == MAX_TASKS) {
            throw new ZhangWeiException("Your list is full: it holds at most "
                    + MAX_TASKS + " tasks.");
        }
        tasks[taskCount] = task;
        taskCount++;
        System.out.println("Got it. I've added this task:");
        printTask(taskCount - 1);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints every stored task, numbered from 1, with its done status. */
    private static void listTasks() {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
    }

    /** Prints the task at the given index, indented, as it appears to the user. */
    private static void printTask(int index) {
        System.out.println("  " + tasks[index]);
    }

    /**
     * Returns the 0-based index of the task the given argument refers to.
     *
     * @param arguments the text typed after the command word, e.g. "2".
     * @throws ZhangWeiException if it is missing, not a number,
     *     or does not refer to an existing task.
     */
    private static int parseTaskIndex(String arguments) throws ZhangWeiException {
        if (arguments.isEmpty()) {
            throw new ZhangWeiException("Which task? For example: mark 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new ZhangWeiException("\"" + arguments + "\" is not a task number. "
                    + "For example: mark 2");
        }

        if (taskCount == 0) {
            throw new ZhangWeiException("You have no tasks yet, so there is no task "
                    + taskNumber + ".");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new ZhangWeiException("There is no task " + taskNumber + ". "
                    + "You have " + taskCount + " tasks.");
        }
        return taskNumber - 1;
    }

    /**
     * Marks the task with the given 1-based number as done and confirms it.
     * Assumes the number refers to an existing task.
     */
    private static void markTask(int taskNumber) {
        int index = taskNumber - 1;
        tasks[index].markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        printTask(index);
    }

    /**
     * Marks the task with the given 1-based number as not done and confirms it.
     * Assumes the number refers to an existing task.
     */
    private static void unmarkTask(int taskNumber) {
        int index = taskNumber - 1;
        tasks[index].markAsNotDone();
        System.out.println("OK, I've marked this task as not done yet:");
        printTask(index);
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
        return new Event(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    public static void main(String[] args) {
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

        Scanner scan = new Scanner(System.in);

        boolean isRunning = true;

        while (isRunning) {
            String input = scan.nextLine();
            if (input.isBlank()) {
                continue;
            }

            // The first word is the command; anything after it is its argument.
            String command = input.split(" ")[0];
            // Everything after the command word, e.g. "return book /by Sunday".
            String arguments = input.substring(command.length()).trim();

            // Anything the chatbot can explain to the user arrives here as a
            // ZhangWeiException, so one handler reports them all.
            try {
                if (command.equals("bye")) {
                    isRunning = false;
                    System.out.println("Bye. Hope to see you again soon!");
                } else if (command.equals("list")) {
                    listTasks();
                } else if (command.equals("mark")) {
                    markTask(parseTaskIndex(arguments) + 1);
                } else if (command.equals("unmark")) {
                    unmarkTask(parseTaskIndex(arguments) + 1);
                } else if (command.equals("todo")) {
                    addTask(parseTodo(arguments));
                } else if (command.equals("deadline")) {
                    addTask(parseDeadline(arguments));
                } else if (command.equals("event")) {
                    addTask(parseEvent(arguments));
                } else {
                    throw new ZhangWeiException("I don't know the command \"" + command
                            + "\". I understand: todo, deadline, event, list, mark, "
                            + "unmark, bye.");
                }
            } catch (ZhangWeiException e) {
                System.out.println(e.getMessage());
            }
        }
        scan.close();
    }
}
