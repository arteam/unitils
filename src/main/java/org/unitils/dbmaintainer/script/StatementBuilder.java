package org.unitils.dbmaintainer.script;

/**
 * A class for building statements.
 *
 * @author Stefan Bangels
 */
public class StatementBuilder {

    private final StringBuilder statement = new StringBuilder();

    private boolean executable;

    /**
     * Returns true if the statement is executable.
     *
     * @return true if the the statement is executable
     */
    public boolean isExecutable() {
        return executable;
    }


    /**
     * Change the statement executable flag.
     *
     * @param executable true if the statement is executable
     */
    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    /**
     * Get the statement.
     *
     * @return the statement
     */
    public String getStatement() {
        return statement.toString();
    }

    /**
     * Append a character to the statement.
     *
     * @param c the character
     */
    public void append(char c) {
        statement.append(c);
    }

    /**
     * Returns the length (character count) of the statement.
     *
     * @return the length (character count) of the statement.
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

}