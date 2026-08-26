import java.util.Scanner;

/**
 * ZhangWei
 * This is a chatbot named ZhangWei
 */
public class ZhangWei {

    private static final int MAX_TASKS = 100;
    private static final String[] tasks = new String[MAX_TASKS];
    private static int taskCount = 0;

    // add task to tasks
    private static void addTask(String task) {
        tasks[taskCount] = task;
        taskCount++;
        System.out.println("added: " + task);
    }

    // list down all tasks
    private static void listTasks() {
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + ". " + tasks[i]);
        }
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
            if (input.equals("bye")) {
                isRunning = false;
                System.out.println("Bye. Hope to see you again soon!");
            } else if (input.equals("list")) {
                listTasks();
            } else {
                addTask(input);
            }
        }
        scan.close();
    }
}
