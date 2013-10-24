/*
 * Copyright 2013,  Unitils.org
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

package org.unitils.database.dbmaintain;

import org.dbmaintain.DbMaintainer;
import org.dbmaintain.MainFactory;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.mock.ArgumentMatchers.anyBoolean;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainWrapperUpdateDatabaseIfNeededTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainWrapper dbMaintainWrapper;

    protected Mock<MainFactory> mainFactoryMock;
    protected Mock<DbMaintainer> dbMaintainerMock;
    protected Mock<DatabaseUpdateListener> databaseUpdateListenerMock;


    @Before
    public void initialize() {
        dbMaintainWrapper = new DbMaintainWrapper(mainFactoryMock.getMock(), true);

        mainFactoryMock.returns(dbMaintainerMock).createDbMaintainer();
    }


    @Test
    public void updateDatabase() {
        dbMaintainerMock.returns(true).updateDatabase(false);

        boolean result = dbMaintainWrapper.updateDatabaseIfNeeded();

        dbMaintainerMock.assertInvoked().updateDatabase(false);
        assertTrue(result);
    }

    @Test
    public void updateDatabaseOnlyPerformedOnce() {
        dbMaintainerMock.returns(true).updateDatabase(false);

        boolean result1 = dbMaintainWrapper.updateDatabaseIfNeeded();
        boolean result2 = dbMaintainWrapper.updateDatabaseIfNeeded();

        dbMaintainerMock.assertInvoked().updateDatabase(false);
        dbMaintainerMock.assertNotInvoked().updateDatabase(anyBoolean());
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void ignoredIfNotEnabled() {
        dbMaintainWrapper = new DbMaintainWrapper(mainFactoryMock.getMock(), false);

        boolean result = dbMaintainWrapper.updateDatabaseIfNeeded();
        dbMaintainerMock.assertNotInvoked().updateDatabase(false);
        assertFalse(result);
    }

    @Test
    public void databaseUpdateListenersCalledIfDatabaseWasUpdated() {
        dbMaintainerMock.returns(true).updateDatabase(false);

        dbMaintainWrapper.registerDatabaseUpdateListener(databaseUpdateListenerMock.getMock());
        dbMaintainWrapper.updateDatabaseIfNeeded();

        databaseUpdateListenerMock.assertInvoked().databaseWasUpdated();
    }

    @Test
    public void databaseUpdateListenersNotCalledIfDatabaseWasNotUpdated() {
        dbMaintainerMock.returns(false).updateDatabase(false);

        dbMaintainWrapper.registerDatabaseUpdateListener(databaseUpdateListenerMock.getMock());
        dbMaintainWrapper.updateDatabaseIfNeeded();

        databaseUpdateListenerMock.assertNotInvoked().databaseWasUpdated();
    }
}
