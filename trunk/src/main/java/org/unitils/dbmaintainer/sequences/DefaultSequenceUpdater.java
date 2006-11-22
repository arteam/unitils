/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbmaintainer.sequences;

import org.apache.commons.configuration.Configuration;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import java.sql.SQLException;
import java.util.Set;

/**
 * Base convenience implementation of {@link SequenceUpdater}
 */
public class DefaultSequenceUpdater extends DatabaseTask implements SequenceUpdater {

    /* Property key for the lowest acceptacle sequence value */
    public static final String PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE = "sequenceUpdater.sequencevalue.lowestacceptable";

    /* The lowest acceptable sequence value */
    protected long lowestAcceptableSequenceValue;

    protected void doInit(Configuration configuration) {
        lowestAcceptableSequenceValue = configuration.getLong(PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE);
    }

    public void updateSequences() throws StatementHandlerException {
        try {
            incrementSequencesWithLowValue();
            if (dbSupport.supportsIdentityColumns()) {
                incrementIdentityColumnsWithLowValue();
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while updating sequences", e);
        }
    }

    private void incrementSequencesWithLowValue() throws SQLException, StatementHandlerException {
        Set<String> sequenceNames = dbSupport.getSequenceNames();
        for (String sequenceName : sequenceNames) {
            if (dbSupport.getNextValueOfSequence(sequenceName) < lowestAcceptableSequenceValue) {
                dbSupport.incrementSequenceToValue(sequenceName, lowestAcceptableSequenceValue);
            }
        }
    }

    private void incrementIdentityColumnsWithLowValue() throws SQLException {
        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            Set<String> primaryKeyColumnNames = dbSupport.getPrimaryKeyColumnNames(tableName);
            if (primaryKeyColumnNames.size() == 1) {
                String primaryKeyColumnName = primaryKeyColumnNames.iterator().next();
                dbSupport.incrementIdentityColumnToValue(tableName, primaryKeyColumnName, lowestAcceptableSequenceValue);
            }
        }
    }


}
