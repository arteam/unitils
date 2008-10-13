package org.unitils.dbmaintainer.script;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * A class for building statements.
 *
 * @author Stefan Bangels
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class StatementBuilder {

    /* The current statement content */
    private StringBuilder statement = new StringBuilder();

    /* True if the the statement is executable */
    private boolean executable;


    /**
     * @return True if the the statement is executable
     */
    public boolean isExecutable() {
        return executable;
    }


    /**
     * Change the statement executable flag.
     *
     * @param executable True if the statement is executable
     */
    public void setExecutable(boolean executable) {
        this.executable = executable;
    }


    /**
     * Append a character to the statement.
     *
     * @param c The character
     */
    public void append(char c) {
        statement.append(c);
    }


    /**
     * Returns the length (character count) of the statement.
     *
     * @return The length (character count) of the statement.
     */
    public int getLength() {
        return statement.length();
    }


    /**
     * Clear the statement.
     */
    public void clear() {
        statement.setLength(0);
    }


    /**
     * Returns the characters that should be removed from the statements. Semi-colons are not part of a statement and
     * should therefore be removed from the statement.
     *
     * @return The separator characters to remove, not null
     */
    public char[] getTrailingSeparatorCharsToRemove() {
        return new char[]{';'};
    }


    /**
     * Creates the resulting statement out of the given characters.
     * This will trim the statement and remove any trailing separtors if needed.
     *
     * @return The resulting statement, null if no statement is left
     */
    public String createStatement() {
        // get built statement to return
        String trimmedStatement = statement.toString().trim();

        // ignore empty statements
        if (isEmpty(trimmedStatement)) {
            return null;
        }

        // remove trailing separator character (eg ;)
        int lastIndex = trimmedStatement.length() - 1;
        char lastChar = trimmedStatement.charAt(lastIndex);
        for (char trailingChar : getTrailingSeparatorCharsToRemove()) {
            if (lastChar == trailingChar) {
                trimmedStatement = trimmedStatement.substring(0, lastIndex);
                break;
            }
        }

        // trim and see if anything is left after removing the trailing separator (eg ;)
        trimmedStatement = trimmedStatement.trim();
        if (isEmpty(trimmedStatement)) {
            return null;
        }
        return trimmedStatement;
    }


}