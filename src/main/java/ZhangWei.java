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
     * Stores a new task of the given type, confirms it,
     * and reports how many tasks are now stored.
     */
    private static void addTask(String description, String typeIcon) {
        tasks[taskCount] = new Task(description, typeIcon);
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
            // The first word is the command; anything after it is its argument.
            String[] words = input.split(" ");
            String command = words[0];

            if (command.equals("bye")) {
                isRunning = false;
                System.out.println("Bye. Hope to see you again soon!");
            } else if (command.equals("list")) {
                listTasks();
            } else if (command.equals("mark")) {
                markTask(Integer.parseInt(words[1]));
            } else if (command.equals("unmark")) {
                unmarkTask(Integer.parseInt(words[1]));
            } else if (command.equals("todo")) {
                // Everything after the command word is the description.
                addTask(input.substring(command.length()).trim(), "T");
            } else {
                addTask(input, "T");
            }
        }
        scan.close();
    }
}
