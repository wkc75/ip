import java.util.Scanner;

public class ZhangWei {
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
        boolean isEcho = true;
        while (isEcho) {
            String input = scan.nextLine();
            if (input.equals("bye")) {
                isEcho = false;
                System.out.println("Bye. Hope to see you again soon!");
            } else {
                System.out.println(input);
            }
        }
        scan.close();
    }
}
