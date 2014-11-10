package org.unitils.jbehave;

import java.io.IOException;

import javax.mail.MessagingException;

import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.unitils.core.Unitils;
import org.unitils.mail.MailModule;
import org.unitils.mail.SmtpServer;
import org.unitils.mail.annotation.TestSmtpServer;


/**
 * A simple mail step.
 * 
 * @author Willemijn Wouters
 * 
 * @since 1.0.0
 * 
 */
//START SNIPPET: jbehavestep
public class SimpleMailStep {
    
    
    @BeforeStories
    public void beforeStories() {
        System.out.println("In before stories: " + getClass());
    } 
    
    
    @TestSmtpServer
    private SmtpServer smtpServer;

    private SimpleSender mailSender;

    private String address;

    @BeforeScenario
    public void beforeScenarioPhase() {
        String port = (String) Unitils.getInstance().getConfiguration().get(MailModule.SMTP_DEFAULT_PORT);
        mailSender = new SimpleSender(Integer.valueOf(port));
    }

    @Given("send an email to $mailAddress")
    public void soSomeSetUp(@Named("mailAddress") String address) throws MessagingException, IOException {
        mailSender.sendMessage("sender@here.com", "TestHeader", "Test Body", address);
        this.address = address;
    }

    @Then("check if the email is sent")
    public void doSomeChecks() throws IOException, MessagingException {
        //... do some assertions...
    }
}
//END SNIPPET: jbehavestep
