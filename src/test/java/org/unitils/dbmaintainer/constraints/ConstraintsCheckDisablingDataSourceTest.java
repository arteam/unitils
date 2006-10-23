package org.unitils.dbmaintainer.constraints;

import static org.easymock.EasyMock.expect;
import org.unitils.UnitilsJUnit3;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.LenientMock;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * todo javadoc
 *
 * @author Filip Neven
 */
@SuppressWarnings({"UnusedDeclaration"})
public class ConstraintsCheckDisablingDataSourceTest extends UnitilsJUnit3 {

    private ConstraintsCheckDisablingDataSource constraintsCheckDisablingDataSource;

    @LenientMock
    private DataSource mockDataSource;

    @LenientMock
    private ConstraintsDisabler mockConstraintsDisabler;

    @LenientMock
    private Connection mockConnection;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        constraintsCheckDisablingDataSource = new ConstraintsCheckDisablingDataSource(mockDataSource, mockConstraintsDisabler);
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
