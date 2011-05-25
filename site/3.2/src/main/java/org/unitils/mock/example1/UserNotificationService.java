package org.unitils.mock.example1;

import org.unitils.reflectionassert.User;

import java.util.List;

public class UserNotificationService {

    private UserDao userDao;
    private MailService mailService;


    public void notifyExpiredUsersByMail() {
        List<User> users = userDao.getUsersWithExpiredLicense();
        for (User user : users) {
            mailService.sendMail(user.getEmail(), "subject", "body");
        }
    }

}
