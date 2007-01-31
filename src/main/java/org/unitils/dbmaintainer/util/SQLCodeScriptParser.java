package org.unitils.dbmaintainer.util;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SQLCodeScriptParser extends BaseScriptParser {

    public SQLCodeScriptParser() {
        super(true, true);
    }

    protected boolean reachedEndOfStatement(char[] script, int currentIndexInScript, StatementBuilder statementBuilder, List<String> statements) {
        return getCurrentChar(script, currentIndexInScript) == '/'
                && (statementBuilder.getLastChar() == '\n' || statementBuilder.getLastChar() == '\r')
                && noFurtherContentOnThisLine(script, currentIndexInScript);
    }

    private boolean noFurtherContentOnThisLine(char[] script, int currentIndexInScript) {
        for (int index = currentIndexInScript + 1; ; index++) {
            if (index >= script.length) {
                return true;
            }
            if (getCurrentChar(script, index) == '\n' || getCurrentChar(script, index) == '\r') {
                return true;
            }
            if (getCurrentChar(script, index) != ' ') {
                return false;
            }
        }
    }
}
