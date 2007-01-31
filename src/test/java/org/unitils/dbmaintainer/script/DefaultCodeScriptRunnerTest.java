package org.unitils.dbmaintainer.script;

import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.dbmaintainer.script.impl.LoggingStatementHandlerDecorator;
import org.unitils.easymock.annotation.RegularMock;
import org.unitils.easymock.annotation.Mock;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.apache.commons.configuration.Configuration;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultCodeScriptRunnerTest extends UnitilsJUnit3 {

    @Mock
    private StatementHandler mockStatementHandler = null;

    /* Tested instance  */
    private CodeScriptRunner codeScriptRunner;

    /* Normal script, containing 2 statements and a blank line */
    private static final String NORMAL_SCRIPT =
            "PROCEDURE TEST1 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "statement1;\n" +
            "statement2;\n" +
            "END\n" +
            "/  \n" +
            "PROCEDURE TEST2 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "statement1;\n" +
            "statement2;\n" +
            "END;\n" +
            "/";

    private static final String SCRIPT_WITH_MULTILINE_COMMENT =
            "/* multiline\n" +
            " comment */" +
            "PROCEDURE TEST1 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "statement1;\n" +
            "statement2;\n" +
            "END;\n" +
            "/";

    private static final String SCRIPT_WITH_LINE_COMMENT =
            "-- line comment /\n" +
            "PROCEDURE TEST1 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "statement1;\n" +
            "statement2;\n" +
            "END;\n" +
            "/";

    private static final String SCRIPT_WITH_SLASH_IN_CODE =
            "PROCEDURE TEST1 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "/statement1/;\n" +
            "statement2;\n" +
            "END;\n" +
            "/\n" +
            "PROCEDURE TEST2 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "/statement1/\n" +
            "statement2;\n" +
            "END\n" +
            "/";


    /**
     * Initialize test fixture
     */
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        StatementHandler loggingStatementHandler = new LoggingStatementHandlerDecorator(mockStatementHandler);
        codeScriptRunner = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(CodeScriptRunner.class, configuration, null, loggingStatementHandler);
    }

    public void testExecute() throws Exception {
        mockStatementHandler.handle("PROCEDURE TEST1 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "statement1;\n" +
            "statement2;\n" +
            "END");
        mockStatementHandler.handle("PROCEDURE TEST2 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "statement1;\n" +
            "statement2;\n" +
            "END;");
        replay();

        codeScriptRunner.execute(NORMAL_SCRIPT);
    }

    public void testExecute_multilineComment() throws Exception {
        mockStatementHandler.handle("/* multiline\n" +
            " comment */" +
            "PROCEDURE TEST1 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "statement1;\n" +
            "statement2;\n" +
            "END;");

        replay();
        codeScriptRunner.execute(SCRIPT_WITH_MULTILINE_COMMENT);
    }

    public void testExecute_lineComment() throws Exception {
        mockStatementHandler.handle("-- line comment /\n" +
            "PROCEDURE TEST1 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "statement1;\n" +
            "statement2;\n" +
            "END;");

        replay();
        codeScriptRunner.execute(SCRIPT_WITH_LINE_COMMENT);
    }

    public void testExecute_scriptWithSlashInCode() throws Exception {
        mockStatementHandler.handle("PROCEDURE TEST1 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "/statement1/;\n" +
            "statement2;\n" +
            "END;");
        mockStatementHandler.handle("PROCEDURE TEST2 (param1 VARCHAR, param2 BLOB) IS\n" +
            "BEGIN\n" +
            "/statement1/\n" +
            "statement2;\n" +
            "END");

        replay();
        codeScriptRunner.execute(SCRIPT_WITH_SLASH_IN_CODE);
    }
}
