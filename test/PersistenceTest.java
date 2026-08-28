import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/** End-to-end checks for loading and saving tasks on the hard disk. */
public class PersistenceTest {

    /** Runs every persistence scenario and reports success if all assertions pass. */
    public static void main(String[] args) throws Exception {
        acceptsIsoDatesAndDisplaysReadableDates();
        rejectsInvalidUserDatesWithoutEndingTheSession();
        loadsTasksWhenTheChatbotStarts();
        startsEmptyWhenNoSaveFileExists();
        ignoresBlankLinesInTheSaveFile();
        skipsDamagedLinesAndKeepsACopy();
        rejectsTheFieldSeparatorInUserInput();
        savesAfterAddingTasks();
        savesAfterMarkingATask();
        savesAfterUnmarkingATask();
        savesAfterDeletingATask();
        System.out.println("All persistence tests passed.");
    }

    /**
     * Verifies that ISO dates are understood, displayed readably, and saved
     * without losing their machine-readable representation.
     */
    private static void acceptsIsoDatesAndDisplaysReadableDates() throws Exception {
        Path workingDirectory = Files.createTempDirectory("zhangwei-date-test-");
        try {
            String output = runChatbot(workingDirectory,
                    "deadline return book /by 2019-12-02\n"
                            + "event project meeting /from 2019-12-03 /to 2019-12-04\n"
                            + "list\nbye\n");

            String expectedList = "Here are the tasks in your list:\n"
                    + "1.[D][ ] return book (by: Dec 2 2019)\n"
                    + "2.[E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)\n";
            if (!output.contains(expectedList)) {
                throw new AssertionError("Expected formatted task dates:\n"
                        + expectedList + "\nActual chatbot output:\n" + output);
            }

            List<String> savedLines = Files.readAllLines(
                    workingDirectory.resolve("data/zhangwei.txt"));
            List<String> expectedLines = List.of(
                    "D | 0 | return book | 2019-12-02",
                    "E | 0 | project meeting | 2019-12-03 | 2019-12-04");
            if (!savedLines.equals(expectedLines)) {
                throw new AssertionError("Expected " + expectedLines
                        + " but found " + savedLines);
            }
        } finally {
            deleteRecursively(workingDirectory);
        }
    }

    /** Verifies that malformed or impossible dates are reported without a crash. */
    private static void rejectsInvalidUserDatesWithoutEndingTheSession() throws Exception {
        Path workingDirectory = Files.createTempDirectory("zhangwei-invalid-date-test-");
        try {
            String output = runChatbot(workingDirectory,
                    "deadline return book /by Sunday\n"
                            + "event meeting /from Monday /to 2019-12-04\n"
                            + "event meeting /from 2019-12-03 /to 2019-02-30\n"
                            + "list\nbye\n");

            String expected = "The /by date must use yyyy-MM-dd, "
                    + "for example 2019-12-02.\n"
                    + "The /from date must use yyyy-MM-dd, "
                    + "for example 2019-12-02.\n"
                    + "The /to date must use yyyy-MM-dd, "
                    + "for example 2019-12-02.\n"
                    + "Here are the tasks in your list:\n"
                    + "Bye. Hope to see you again soon!\n";
            if (!output.contains(expected)) {
                throw new AssertionError("Expected invalid dates to be reported and "
                        + "the session to continue:\n" + expected
                        + "\nActual chatbot output:\n" + output);
            }
        } finally {
            deleteRecursively(workingDirectory);
        }
    }

    /** Verifies that a fresh chatbot process restores every saved task. */
    private static void loadsTasksWhenTheChatbotStarts() throws Exception {
        Path workingDirectory = Files.createTempDirectory("zhangwei-loading-test-");
        try {
            Path savedFile = workingDirectory.resolve("data/zhangwei.txt");
            Files.createDirectories(savedFile.getParent());
            Files.write(savedFile, List.of(
                    "T | 1 | read book",
                    "D | 0 | return book | 2019-06-06",
                    "E | 1 | project meeting | 2019-08-06 | 2019-08-07"));

            String output = runChatbot(workingDirectory, "list\nbye\n");
            String expectedList = "Here are the tasks in your list:\n"
                    + "1.[T][X] read book\n"
                    + "2.[D][ ] return book (by: Jun 6 2019)\n"
                    + "3.[E][X] project meeting (from: Aug 6 2019 to: Aug 7 2019)\n";
            if (!output.contains(expectedList)) {
                throw new AssertionError("Expected restored task list:\n"
                        + expectedList + "\nActual chatbot output:\n" + output);
            }
        } finally {
            deleteRecursively(workingDirectory);
        }
    }

    /** Verifies that each supported task type is written in the chosen file format. */
    private static void savesAfterAddingTasks() throws Exception {
        assertSavedLines(
                "todo read book\n"
                        + "deadline return book /by 2019-06-06\n"
                        + "event project meeting /from 2019-08-06 /to 2019-08-07\n"
                        + "bye\n",
                List.of(
                        "T | 0 | read book",
                        "D | 0 | return book | 2019-06-06",
                        "E | 0 | project meeting | 2019-08-06 | 2019-08-07"));
    }

    /** Verifies that changing a task to done updates the saved status. */
    private static void savesAfterMarkingATask() throws Exception {
        assertSavedLines(
                "todo read book\nmark 1\nbye\n",
                List.of("T | 1 | read book"));
    }

    /** Verifies that changing a task back to not done updates the saved status. */
    private static void savesAfterUnmarkingATask() throws Exception {
        assertSavedLines(
                "todo read book\nmark 1\nunmark 1\nbye\n",
                List.of("T | 0 | read book"));
    }

    /** Verifies that deleting a task removes it from the saved snapshot. */
    private static void savesAfterDeletingATask() throws Exception {
        assertSavedLines(
                "todo read book\ntodo write code\ndelete 1\nbye\n",
                List.of("T | 0 | write code"));
    }

    /**
     * Runs a fresh chatbot process and compares its saved file with the expected lines.
     */
    private static void assertSavedLines(String input, List<String> expected) throws Exception {
        Path workingDirectory = Files.createTempDirectory("zhangwei-persistence-test-");
        try {
            runChatbot(workingDirectory, input);

            Path savedFile = workingDirectory.resolve("data/zhangwei.txt");
            if (!Files.exists(savedFile)) {
                throw new AssertionError("Expected saved file does not exist: " + savedFile);
            }

            List<String> actual = Files.readAllLines(savedFile);
            if (!actual.equals(expected)) {
                throw new AssertionError("Expected " + expected + " but found " + actual);
            }
        } finally {
            deleteRecursively(workingDirectory);
        }
    }

    /** Runs the real chatbot in the supplied working directory. */
    private static String runChatbot(Path workingDirectory, String input) throws Exception {
        Process process = new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp",
                System.getProperty("java.class.path"),
                "ZhangWei")
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();
        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();

        String output = new String(process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new AssertionError("Chatbot exited with " + exitCode + ":\n" + output);
        }
        return output;
    }

    /** Verifies that a first run with no save file starts cleanly and empty. */
    private static void startsEmptyWhenNoSaveFileExists() throws Exception {
        Path workingDirectory = Files.createTempDirectory("zhangwei-missing-file-test-");
        try {
            String output = runChatbot(workingDirectory, "list\nbye\n");
            if (!output.contains("Here are the tasks in your list:")) {
                throw new AssertionError("Expected an empty list, got:\n" + output);
            }
            if (output.contains("could not")) {
                throw new AssertionError("A missing save file should not be "
                        + "reported as a problem:\n" + output);
            }
        } finally {
            deleteRecursively(workingDirectory);
        }
    }

    /** Verifies that blank lines are ignored rather than treated as damage. */
    private static void ignoresBlankLinesInTheSaveFile() throws Exception {
        Path workingDirectory = Files.createTempDirectory("zhangwei-blank-line-test-");
        try {
            Path savedFile = workingDirectory.resolve("data/zhangwei.txt");
            Files.createDirectories(savedFile.getParent());
            Files.write(savedFile, List.of("", "T | 0 | read book", "", ""));

            String output = runChatbot(workingDirectory, "list\nbye\n");
            if (!output.contains("1.[T][ ] read book")) {
                throw new AssertionError("Expected the one real task, got:\n" + output);
            }
            if (output.contains("could not understand")) {
                throw new AssertionError("Blank lines should not count as damage:\n"
                        + output);
            }
        } finally {
            deleteRecursively(workingDirectory);
        }
    }

    /**
     * Verifies that unreadable lines are skipped, reported, and preserved in a
     * backup copy before a later save can overwrite them.
     */
    private static void skipsDamagedLinesAndKeepsACopy() throws Exception {
        Path workingDirectory = Files.createTempDirectory("zhangwei-damaged-test-");
        try {
            Path savedFile = workingDirectory.resolve("data/zhangwei.txt");
            Files.createDirectories(savedFile.getParent());
            Files.write(savedFile, List.of(
                    "T | 1 | read book",
                    "D | 0 | missing its by field",
                    "X | 0 | unknown task type",
                    "T | 7 | status is not 1 or 0",
                    "D | 0 | impossible date | 2019-02-30",
                    "E | 0 | project meeting | 2019-08-06 | 2019-08-07"));

            String output = runChatbot(workingDirectory, "list\nbye\n");

            if (!output.contains("I could not understand 4 line(s)")) {
                throw new AssertionError("Expected 4 damaged lines to be "
                        + "reported, got:\n" + output);
            }

            String expectedList = "Here are the tasks in your list:\n"
                    + "1.[T][X] read book\n"
                    + "2.[E][ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)\n";
            if (!output.contains(expectedList)) {
                throw new AssertionError("Expected the two readable tasks:\n"
                        + expectedList + "\nActual chatbot output:\n" + output);
            }

            Path backupFile = workingDirectory.resolve("data/zhangwei.txt.corrupt");
            if (!Files.exists(backupFile)) {
                throw new AssertionError("Expected a backup of the damaged file at "
                        + backupFile);
            }
            if (Files.readAllLines(backupFile).size() != 6) {
                throw new AssertionError("The backup should hold all 6 original lines");
            }
        } finally {
            deleteRecursively(workingDirectory);
        }
    }

    /**
     * Verifies that a description containing the field separator is refused,
     * rather than being saved as a line that cannot be read back.
     */
    private static void rejectsTheFieldSeparatorInUserInput() throws Exception {
        Path workingDirectory = Files.createTempDirectory("zhangwei-separator-test-");
        try {
            String output = runChatbot(workingDirectory,
                    "todo read | book\ntodo read book\nbye\n");

            if (!output.contains("cannot contain")) {
                throw new AssertionError("Expected the separator to be refused:\n"
                        + output);
            }

            Path savedFile = workingDirectory.resolve("data/zhangwei.txt");
            List<String> actual = Files.readAllLines(savedFile);
            if (!actual.equals(List.of("T | 0 | read book"))) {
                throw new AssertionError("Only the valid task should be saved, found "
                        + actual);
            }
        } finally {
            deleteRecursively(workingDirectory);
        }
    }

    /** Removes the temporary files created by one test scenario. */
    private static void deleteRecursively(Path directory) throws IOException {
        try (var paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
