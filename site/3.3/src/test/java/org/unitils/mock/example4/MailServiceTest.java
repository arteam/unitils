package org.unitils.mock.example4;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.PartialMock;

// START SNIPPET: mailServiceTest
public class MailServiceTest extends UnitilsJUnit4 {

    private PartialMock<MailService> mailService;

    @Test
    public void testMethod() {
        mailService.stub().doSendMail(null, null, null);

        mailService.getMock().sendMessageToUser("some.user@mail.com");

        mailService.assertInvoked().doSendMail("some.user@mail.com", "some subject", "some content");
    }
}
// END SNIPPET: mailServiceTest

