package zhangwei.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import zhangwei.ZhangWeiException;
import zhangwei.task.Deadline;
import zhangwei.task.Event;
import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.task.Todo;

/**
 * Loads and saves the task list using a text file on the hard disk.
 *
 * <p>Each task occupies one line, with fields separated by " | ", e.g.
 * {@code D | 0 | return book | 2019-06-06}. The first field is the task type,
 * the second is the done status (1 or 0), and the rest depend on the type.
 */
public class Storage {

    /** Separates the fields within one saved line. */
    private static final String SEPARATOR = " | ";

    /** Appended to the save file's name when a copy is kept of a damaged file. */
    private static final String BACKUP_SUFFIX = ".corrupt";

    private final Path filePath;

    /** Creates storage that reads and writes the given path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * What a load produced: the tasks recovered, how many lines could not be
     * understood, and where the damaged file was copied to (null if nothing
     * was damaged, or if the copy itself failed).
     *
     * <p>A record is used because this is a plain group of values with no
     * behaviour; it gives the constructor, accessors and equals for free.
     */
    public record LoadResult(List<Task> tasks, int skippedLines, Path backupPath) {
    }

    /**
     * Loads the tasks in the save file.
     *
     * <p>A missing file is not an error: it simply means nothing has been saved
     * yet, so an empty list is returned. Lines that cannot be understood are
     * skipped rather than aborting the load, so one damaged line does not cost
     * the user every other task. Because the next save would overwrite those
     * skipped lines, a copy of the original file is kept first.
     *
     * @throws ZhangWeiException if the file exists but cannot be read at all.
     */
    public LoadResult loadTasks() throws ZhangWeiException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return new LoadResult(tasks, 0, null);
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new ZhangWeiException("I could not read your saved tasks from "
                    + filePath + " (" + e.getMessage() + ").");
        }

        int skippedLines = 0;
        for (String line : lines) {
            // A blank line carries no task, so it is ignored rather than
            // counted as damage.
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (ZhangWeiException e) {
                skippedLines++;
            }
        }

        Path backupPath = skippedLines > 0 ? backUpDamagedFile() : null;
        return new LoadResult(tasks, skippedLines, backupPath);
    }

    /**
     * Replaces the save file with one line for each task in the current list,
     * creating the containing folder first if it does not exist yet.
     *
     * @throws ZhangWeiException if the tasks could not be written.
     */
    public void saveTasks(TaskList tasks) throws ZhangWeiException {
        try {
            // getParent() is null for a bare file name such as "tasks.txt",
            // in which case there is no folder to create.
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks.asList()) {
                lines.add(formatTask(task));
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new ZhangWeiException("I could not save your tasks to "
                    + filePath + " (" + e.getMessage() + "). "
                    + "Your changes will be lost when you exit.");
        }
    }

    /**
     * Copies the save file alongside itself before damaged lines are lost to
     * the next save. Returns the copy's path, or null if the copy failed --
     * a failed backup is not worth stopping the chatbot for.
     */
    private Path backUpDamagedFile() {
        Path backupPath = filePath.resolveSibling(filePath.getFileName() + BACKUP_SUFFIX);
        try {
            Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            return backupPath;
        } catch (IOException e) {
            return null;
        }
    }

    /** Converts one task into the text format used in the save file. */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return "T" + SEPARATOR + status + SEPARATOR + task.getDescription();
        } else if (task instanceof Deadline deadline) {
            return "D" + SEPARATOR + status + SEPARATOR + task.getDescription()
                    + SEPARATOR + deadline.getBy();
        } else if (task instanceof Event event) {
            return "E" + SEPARATOR + status + SEPARATOR + task.getDescription()
                    + SEPARATOR + event.getFrom() + SEPARATOR + event.getTo();
        }
        // Reaching here means a new Task subclass was added without teaching
        // this method to save it, which is a programming error, not bad input.
        throw new IllegalStateException("Unsupported task type: "
                + task.getClass().getSimpleName());
    }

    /**
     * Recreates one task from a line in the save file.
     *
     * @throws ZhangWeiException if the line is not in the expected format.
     */
    private Task parseTask(String line) throws ZhangWeiException {
        // The -1 limit keeps trailing empty fields, so "T | 0 | " is seen as a
        // blank description rather than a two-field line.
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new ZhangWeiException("expected at least 3 fields");
        }

        String type = fields[0];
        boolean isDone = parseStatus(fields[1]);
        String description = requireText(fields[2], "description");

        Task task = switch (type) {
        case "T" -> {
            requireFieldCount(fields, 3);
            yield new Todo(description);
        }
        case "D" -> {
            requireFieldCount(fields, 4);
            yield new Deadline(description, parseDate(fields[3], "by"));
        }
        case "E" -> {
            requireFieldCount(fields, 5);
            yield new Event(description,
                    parseDate(fields[3], "from"),
                    parseDate(fields[4], "to"));
        }
        default -> throw new ZhangWeiException("unknown task type \"" + type + "\"");
        };

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a saved date written in the ISO {@code yyyy-MM-dd} format.
     *
     * @throws ZhangWeiException if the field is blank, malformed, or impossible.
     */
    private LocalDate parseDate(String field, String fieldName)
            throws ZhangWeiException {
        String date = requireText(field, fieldName);
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new ZhangWeiException(fieldName + " must use yyyy-MM-dd");
        }
    }

    /**
     * Returns the done status a saved line records.
     *
     * @throws ZhangWeiException if the field is neither "1" nor "0". Defaulting
     *     to "not done" instead would silently discard the user's progress.
     */
    private boolean parseStatus(String field) throws ZhangWeiException {
        return switch (field) {
        case "1" -> true;
        case "0" -> false;
        default -> throw new ZhangWeiException("status must be 1 or 0, found \""
                + field + "\"");
        };
    }

    /**
     * Returns the given field unchanged.
     *
     * @throws ZhangWeiException if it is blank, which would produce a task the
     *     user cannot make sense of.
     */
    private String requireText(String field, String fieldName) throws ZhangWeiException {
        if (field.isBlank()) {
            throw new ZhangWeiException(fieldName + " is blank");
        }
        return field;
    }

    /**
     * Checks that a line has exactly the number of fields its type needs.
     *
     * @throws ZhangWeiException if it has too few or too many. Too many usually
     *     means the user typed the separator inside a description.
     */
    private void requireFieldCount(String[] fields, int expected) throws ZhangWeiException {
        if (fields.length != expected) {
            throw new ZhangWeiException("expected " + expected + " fields, found "
                    + fields.length);
        }
    }
}
