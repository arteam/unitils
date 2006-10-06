package org.unitils.dbmaintainer.constraints;

import org.unitils.UnitilsJUnit3;
import org.unitils.easymock.annotation.Mock;
import static org.easymock.EasyMock.*;
import static org.unitils.easymock.EasyMockModule.*;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Filip Neven
 */
public class ConstraintsCheckDisablingDataSourceTest extends UnitilsJUnit3 {

    ConstraintsCheckDisablingDataSource constraintsCheckDisablingDataSource;

    @Mock
    private DataSource mockDataSource;

    @Mock
    private ConstraintsDisabler mockConstraintsDisabler;

    @Mock
    private Connection mockConnection;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        constraintsCheckDisablingDataSource = new ConstraintsCheckDisablingDataSource(mockDataSource,
                mockConstraintsDisabler);
    }

    public void testGetConnection() throws Exception {
        expect(mockDataSource.getConnection()).andReturn(mockConnection);
        mockConstraintsDisabler.disableConstraintsOnConnection(mockConnection);
        replay();

        constraintsCheckDisablingDataSource.getConnection();
    }

    public void testGetConnectionWithUserNameAndPassword() throws Exception {
        expect(mockDataSource.getConnection(null, null)).andReturn(mockConnection);
        mockConstraintsDisabler.disableConstraintsOnConnection(mockConnection);
        replay();

        constraintsCheckDisablingDataSource.getConnection("testUserName", "testPassword");
    }

}
