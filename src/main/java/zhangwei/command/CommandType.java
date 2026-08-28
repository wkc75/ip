package zhangwei.command;

import zhangwei.ZhangWeiException;

/**
 * The commands the chatbot understands, each paired with the keyword the user
 * types for it.
 *
 * <p>Keeping the set of keywords in one enum means the parser and the
 * "I don't know that command" message can never disagree about which commands
 * exist: the message is generated from the same values the parser matches
 * against.
 *
 * <p>This enum names the commands; the {@link Command} classes carry out what
 * each of them does.
 */
public enum CommandType {
    /** Adds a task with no date attached to it. */
    TODO("todo"),
    /** Adds a task that must be done by a given date. */
    DEADLINE("deadline"),
    /** Adds a task that runs between two given dates. */
    EVENT("event"),
    /** Shows every task currently in the list. */
    LIST("list"),
    /** Marks a numbered task as done. */
    MARK("mark"),
    /** Marks a numbered task as not done. */
    UNMARK("unmark"),
    /** Removes a numbered task from the list. */
    DELETE("delete"),
    /** Ends the session. */
    BYE("bye");

    private final String keyword;

    /**
     * Creates a command type invoked by the given keyword.
     *
     * @param keyword the word the user types to invoke this command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword of this command, e.g. "deadline".
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the command invoked by the given keyword.
     *
     * @param keyword the first word of the line the user typed.
     * @return the command type using that keyword.
     * @throws ZhangWeiException if no command uses that keyword, listing the
     *     keywords that would have worked.
     */
    public static CommandType fromKeyword(String keyword) throws ZhangWeiException {
        for (CommandType command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }
        throw new ZhangWeiException("I don't know the command \"" + keyword
                + "\". I understand: " + listKeywords() + ".");
    }

    /**
     * Returns every keyword, comma separated, in the order declared above.
     *
     * @return the keywords of all commands, e.g. "todo, deadline, event".
     */
    private static String listKeywords() {
        StringBuilder keywords = new StringBuilder();
        for (CommandType command : values()) {
            if (keywords.length() > 0) {
                keywords.append(", ");
            }
            keywords.append(command.keyword);
        }
        return keywords.toString();
    }
}
