package org.unitils.mock.example4;

// START SNIPPET: mailService
public class MailService {

    public void sendMessageToUser(String emailAddress) {
        // ... compose the mail etc ...
        // END SNIPPET: mailService
        String subject = "";
        String content = "";
        // START SNIPPET: mailService
        doSendMail(emailAddress, subject, content);
    }

    protected void doSendMail(String emailAddress, String subject, String content) {
        // ... actual sending ...
    }
}
// END SNIPPET: mailService
