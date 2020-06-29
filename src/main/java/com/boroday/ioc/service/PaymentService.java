package com.boroday.ioc.service;

public class PaymentService {
    private MailService mailService;
    private int maxAmount;

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void pay(String from, String to, double amount) {

    }
}
