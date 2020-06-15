package com.boroday.dependencyinjection.service;

public class UserService {
    private MailService mailService;

    public UserService() {
        System.out.println("UserService object created");
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void sendEmailWithUserCount() {
        int numberOfUsersInSystem = getUsersCount();
        mailService.sendEmail("meinEmail@gmail.com", "There are " + numberOfUsersInSystem + " users in system");
    }

    private int getUsersCount() {
        return (int) (Math.random() * 1000);
    }
}
