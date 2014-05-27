package org.unitils.mail;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.mail.annotation.TestSmtpServer;
/**
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 */
@Ignore
//START SNIPPET: mailExample
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class MailModuleIntTest {

    @TestSmtpServer
    private SmtpServer simpleSmtpServer;
    

    @Test
    public void testSendEmail() throws javax.mail.MessagingException, IOException, javax.mail.MessagingException {
        // Some action that makes your code send a mail
        // make sure that the same port as org.unitils.mail.port is used
        // CODE OMITTED

        assertEquals(1, simpleSmtpServer.getReceivedEmailSize());

    }
}
//END SNIPPET: mailExample
