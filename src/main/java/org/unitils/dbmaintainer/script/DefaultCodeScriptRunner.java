package org.unitils.dbmaintainer.script;

import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.core.UnitilsException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;

import java.util.List;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultCodeScriptRunner extends BaseScriptRunner implements CodeScriptRunner {

    protected List<String> parseStatements(String script) {
        return dbSupport.parseCodeStatements(script);
    }
}
