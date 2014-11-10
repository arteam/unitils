package org.unitils.jbehave;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * This class creates and sends the email. Only here to use in proper tests to send some mails to the smpt server.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 */
public class SimpleSender {

    public static  Log LOGGER =  LogFactory.getLog(SimpleSender.class);

    private int port;

    /**
     * @param port
     */
    public SimpleSender(int port) {
        if (port != 0) {
            this.port = port;
        } else {
            this.port = 25;
        }

    }

    /**
     * This method will return the mail properties.
     * 
     * @param port
     * @return {@link Properties}
     */
    private Properties getMailProperties() {
        Properties mailProps = new Properties();
        mailProps.setProperty("mail.smtp.host", "localhost");
        mailProps.setProperty("mail.smtp.port", "" + port);
        mailProps.setProperty("mail.smtp.sendpartial", "true");
        return mailProps;
    }

    /**
     * This method will send the email without attachments.
     * 
     * @param from of type {@link String}
     * @param subject of type {@link String}
     * @param body of type {@link String}
     * @param to of type {@link Array}
     * @param cc of type {@link Array}
     * @param bcc of type {@link Array}
     * @throws MessagingException
     * @throws IOException
     */
    public void sendMessage(String from, String subject, String body, String[] to, String[] cc, String[] bcc) throws MessagingException, IOException {
        // this.sendMessage(from, subject, body, to, new File[]{});
        String[] tempTo, tempCc, tempBcc;
        if (to != null && to.length > 0) {
            tempTo = to;
        } else {
            tempTo = new String[0];
        }
        if (cc != null && cc.length > 0) {
            tempCc = cc;
        } else {
            tempCc = new String[0];
        }
        if (bcc != null && bcc.length > 0) {
            tempBcc = bcc;
        } else {
            tempBcc = new String[0];
        }

        this.sendMessage(from, subject, body, tempTo, tempCc, tempBcc, new File[]{});


    }

    /**
     * This method makes a new {@link MimeMessage} and sends the mail.
     * @param from
     * @param subject
     * @param body
     * @param to
     * @throws MessagingException
     * @throws IOException
     */
    public void sendMessage(String from, String subject, String body, String to) throws MessagingException, IOException {
        this.sendMessage(from, subject, body, new String[]{to}, new String[0], new String[0]);

    }

    /**
     * This method makes a new {@link MimeMessage} and sends the mail.
     * @param from of type {@link String}
     * @param subject of type {@link String}
     * @param body of type {@link String}
     * @param to of type {@link Array}
     * @param cc of type {@link Array}
     * @param bcc of type {@link Array}
     * @param attachements of type {@link Array}
     * @throws MessagingException
     * @throws IOException
     */
    public void sendMessage(String from, String subject, String body, String[] to, String[] cc, String[] bcc, File... attachements) throws MessagingException, IOException {
        Properties mailProps = getMailProperties();
        Session session = Session.getInstance(mailProps);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(body);

        if (to != null && to.length > 0) {
            for (String sendTo : to) {
                msg.addRecipient(RecipientType.TO, new InternetAddress(sendTo));
            }
        }
        if (cc != null && cc.length > 0) {
            for (String sendCC : cc) {
                msg.addRecipient(RecipientType.CC, new InternetAddress(sendCC));
            }
        }
        if (bcc != null && bcc.length > 0) {
            for (String sendBcc : bcc) {
                msg.addRecipient(RecipientType.BCC, new InternetAddress(sendBcc));
            }
        }

        // set Attachment
        if (attachements != null && attachements.length > 0) {
            Multipart multipart = new MimeMultipart();

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart attachementPart = new MimeBodyPart();
            for (File attachement : attachements) {
                attachementPart.attachFile(attachement);
                multipart.addBodyPart(attachementPart);
            }

            msg.setContent(multipart);
        }
        Transport.send(msg);
        failsafeSleep();

    }
    
    public void sendWithSpringHelper(String from, String subject, String body, String[] to, String[] cc, String[] bcc, File... attachements) throws AddressException, MessagingException, IOException {
        Properties mailProps = getMailProperties();
        Session session = Session.getInstance(mailProps);

        MimeMessage msg = new MimeMessage(session);
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(new InternetAddress(from));
        helper.setSubject(subject);
        helper.setSentDate(new Date());
        helper.setText(body);

        if (to != null && to.length > 0) {
            for (String sendTo : to) {
                helper.addTo(new InternetAddress(sendTo));
            }
        }
        if (cc != null && cc.length > 0) {
            for (String sendCC : cc) {
                msg.addRecipient(RecipientType.CC, new InternetAddress(sendCC));
            }
        }
        if (bcc != null && bcc.length > 0) {
            for (String sendBcc : bcc) {
                helper.addBcc(new InternetAddress(sendBcc));
            }
        }

        // set Attachment
        if (attachements != null && attachements.length > 0) {
            Multipart multipart = new MimeMultipart();

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);

            int i = 0;
            for (File attachement : attachements) {
                helper.addAttachment("TestFile-" + i, new FileDataSource(attachement));
            }
            
            msg.setContent(multipart);
        }
        Transport.send(msg);
    }

    private void failsafeSleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // just a small sleep so it seems real.
        }

    }

}
