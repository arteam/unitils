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
package org.unitils.dbmaintainer.structure;

import static org.easymock.EasyMock.expect;
import org.unitils.UnitilsJUnit3;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.Mock;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Test for the {@link ConstraintsCheckDisablingDataSource}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ConstraintsCheckDisablingDataSourceTest extends UnitilsJUnit3 {

    /* Tested object */
    private ConstraintsCheckDisablingDataSource constraintsCheckDisablingDataSource;

    @Mock
    private DataSource mockDataSource;

    @Mock
    private ConstraintsDisabler mockConstraintsDisabler;

    @Mock
    private Connection mockConnection;


    /**
     * Test fixture. Sets up the ConstraintsCheckDisablingDataSource with mocks for the decorated DataSource and
     * ConstraintsDisabler
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        constraintsCheckDisablingDataSource = new ConstraintsCheckDisablingDataSource(mockDataSource, mockConstraintsDisabler);
    }


    /**
     * Wether a connection is correctly retrieved from the decorated DataSource and that constraints checking is
     * disabled on the connection.
     */
    public void testGetConnection() throws Exception {
        expect(mockDataSource.getConnection()).andReturn(mockConnection);
        mockConstraintsDisabler.disableConstraintsOnConnection(mockConnection);
        replay();

        constraintsCheckDisablingDataSource.getConnection();
    }


    /**
     * Wether a connection is correctly retrieved from the decorated DataSource and that constraints checking is
     * disabled on the connection, in case a custom username and password are passed.
     */
    public void testGetConnectionWithUserNameAndPassword() throws Exception {
        expect(mockDataSource.getConnection(null, null)).andReturn(mockConnection);
        mockConstraintsDisabler.disableConstraintsOnConnection(mockConnection);
        replay();

        constraintsCheckDisablingDataSource.getConnection("testUserName", "testPassword");
    }


}
