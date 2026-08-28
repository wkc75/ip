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
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    BYE("bye");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /** Returns the word the user types to invoke this command. */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the command invoked by the given keyword.
     *
     * @throws ZhangWeiException if no command uses that keyword.
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

    /** Returns every keyword, comma separated, in the order declared above. */
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
