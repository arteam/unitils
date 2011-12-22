package org.unitils.mock.example1;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.reflectionassert.User;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UserNotificationServiceTest extends UnitilsJUnit4 {

    @TestedObject
    private UserNotificationService userNotificationService;

    @InjectIntoByType
    private Mock<UserDao> userDaoMock;
    @InjectIntoByType
    private Mock<MailService> mailServiceMock;

    private User user1;
    private User user2;

    @Before
    public void initialize() {
        user1 = new User(1, "first", "last");
        user1.setEmail("john@company.com");
        user2 = new User(2, "first", "last");
        user2.setEmail("jane@organization.org");
    }

    @Test
    public void notifyExpiredUsers() {
        userDaoMock.returns(asList(user1, user2)).getUsersWithExpiredLicense();

        userNotificationService.notifyExpiredUsersByMail();

        mailServiceMock.assertInvoked().sendMail("jeff@test.com", null, null);
    }

    @Test
    public void logScenarioReport() {
        // START SNIPPET: reports
        userDaoMock.returns(asList(user1, user2)).getUsersWithExpiredLicense();

        userNotificationService.notifyExpiredUsersByMail();

        MockUnitils.logFullScenarioReport();
        MockUnitils.logObservedScenario();
        MockUnitils.logDetailedObservedScenario();
        MockUnitils.logSuggestedAsserts();
        // END SNIPPET: reports

    }
}
