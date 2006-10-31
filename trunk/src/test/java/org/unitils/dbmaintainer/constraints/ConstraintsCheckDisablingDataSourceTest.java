package org.unitils.dbmaintainer.constraints;

import static org.easymock.EasyMock.expect;
import org.unitils.UnitilsJUnit3;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.LenientMock;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Test for the ConstraintsCheckDisablingDataSource
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ConstraintsCheckDisablingDataSourceTest extends UnitilsJUnit3 {

    /**
     * Tested object
     */
    private ConstraintsCheckDisablingDataSource constraintsCheckDisablingDataSource;

    @LenientMock
    private DataSource mockDataSource;

    @LenientMock
    private ConstraintsDisabler mockConstraintsDisabler;

    @LenientMock
    private Connection mockConnection;

    /**
     * Test fixture. Sets up the ConstraintsCheckDisablingDataSource with mocks for the decorated DataSource and
     * ConstraintsDisabler
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        constraintsCheckDisablingDataSource = new ConstraintsCheckDisablingDataSource(mockDataSource, mockConstraintsDisabler);
    }

    /**
     * Wether a connection is correctly retrieved from the decorated DataSource and that constraints checking is
     * disabled on the connection.
     *
     * @throws Exception
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
     *
     * @throws Exception
     */
    public void testGetConnectionWithUserNameAndPassword() throws Exception {
        expect(mockDataSource.getConnection(null, null)).andReturn(mockConnection);
        mockConstraintsDisabler.disableConstraintsOnConnection(mockConnection);
        replay();

        constraintsCheckDisablingDataSource.getConnection("testUserName", "testPassword");
    }

}
