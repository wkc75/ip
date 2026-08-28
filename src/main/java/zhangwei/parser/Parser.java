package zhangwei.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import zhangwei.ZhangWeiException;
import zhangwei.command.AddCommand;
import zhangwei.command.Command;
import zhangwei.command.CommandType;
import zhangwei.command.DeleteCommand;
import zhangwei.command.ExitCommand;
import zhangwei.command.ListCommand;
import zhangwei.command.MarkCommand;
import zhangwei.command.UnmarkCommand;
import zhangwei.task.Deadline;
import zhangwei.task.Event;
import zhangwei.task.Todo;

/**
 * Makes sense of the text the user types, turning one line into the
 * {@link Command} it asks for.
 *
 * <p>Only what can be judged from the text alone is checked here: that the
 * keyword exists, that a task number is a number, that a deadline was given a
 * description and a /by. Whether task 5 exists depends on the task list, so
 * that check belongs to the command instead.
 *
 * <p>The methods are static because parsing needs nothing remembered between
 * calls; the parser has no state of its own to hold.
 */
public class Parser {

    /**
     * Returns the command the given line asks for.
     *
     * @param fullCommand one line exactly as the user typed it.
     * @throws ZhangWeiException if the line does not describe a usable command.
     */
    public static Command parse(String fullCommand) throws ZhangWeiException {
        // The first word is the command keyword; the rest is its argument.
        String keyword = fullCommand.split(" ")[0];
        // Everything after the keyword, e.g. "return book /by 2019-12-02".
        String arguments = fullCommand.substring(keyword.length()).trim();

        CommandType type = CommandType.fromKeyword(keyword);
        return switch (type) {
        case BYE -> new ExitCommand();
        case LIST -> new ListCommand();
        case MARK -> new MarkCommand(parseTaskNumber(arguments, type));
        case UNMARK -> new UnmarkCommand(parseTaskNumber(arguments, type));
        case DELETE -> new DeleteCommand(parseTaskNumber(arguments, type));
        case TODO -> new AddCommand(parseTodo(arguments));
        case DEADLINE -> new AddCommand(parseDeadline(arguments));
        case EVENT -> new AddCommand(parseEvent(arguments));
        };
    }

    /**
     * Returns the 1-based task number the given argument names.
     *
     * @param arguments the text typed after the command word, e.g. "2".
     * @param type the command asking, used to phrase the example.
     * @throws ZhangWeiException if it is missing or is not a number.
     */
    private static int parseTaskNumber(String arguments, CommandType type)
            throws ZhangWeiException {
        String example = "For example: " + type.getKeyword() + " 2";
        if (arguments.isEmpty()) {
            throw new ZhangWeiException("Which task? " + example);
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new ZhangWeiException("\"" + arguments + "\" is not a task number. "
                    + example);
        }
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
     * "description /by yyyy-MM-dd".
     *
     * @throws ZhangWeiException if the description or /by date is invalid.
     */
    private static Deadline parseDeadline(String arguments) throws ZhangWeiException {
        String[] parts = arguments.split("/by", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new ZhangWeiException("A deadline needs a description and a /by. "
                    + "For example: deadline return book /by 2019-12-02");
        }
        rejectSeparator(arguments);
        LocalDate by = parseDate(parts[1].trim(), "/by");
        return new Deadline(parts[0].trim(), by);
    }

    /**
     * Returns an event built from text of the form
     * "description /from yyyy-MM-dd /to yyyy-MM-dd".
     *
     * @throws ZhangWeiException if the description or either date is invalid.
     */
    private static Event parseEvent(String arguments) throws ZhangWeiException {
        String[] parts = arguments.split("/from|/to");
        if (parts.length < 3 || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
            throw new ZhangWeiException("An event needs a description, a /from and a /to. "
                    + "For example: event project meeting /from 2019-12-03 "
                    + "/to 2019-12-04");
        }
        rejectSeparator(arguments);
        LocalDate from = parseDate(parts[1].trim(), "/from");
        LocalDate to = parseDate(parts[2].trim(), "/to");
        return new Event(parts[0].trim(), from, to);
    }

    /**
     * Parses a date written in the ISO {@code yyyy-MM-dd} format.
     *
     * @throws ZhangWeiException if the date is missing, malformed, or impossible.
     */
    private static LocalDate parseDate(String text, String marker)
            throws ZhangWeiException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new ZhangWeiException("The " + marker
                    + " date must use yyyy-MM-dd, for example 2019-12-02.");
        }
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
}
