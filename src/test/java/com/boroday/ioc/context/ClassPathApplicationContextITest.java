package com.boroday.ioc.context;

import com.boroday.ioc.service.MailService;
import com.boroday.ioc.service.PaymentService;
import com.boroday.ioc.service.UserService;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassPathApplicationContextITest {
    String[] pathToContextFile = {"src/test/resources/context.xml"};
    ApplicationContext applicationContext;


    @Test
    public void testGetBeanNames() {
        //prepare
        applicationContext = new ClassPathApplicationContext(pathToContextFile);
        List<String> beanNamesForValidation = new ArrayList<>();
        beanNamesForValidation.add("mailService");
        beanNamesForValidation.add("userService");
        beanNamesForValidation.add("paymentWithMaxService");
        beanNamesForValidation.add("paymentService");

        //when
        List<String> beanNames = applicationContext.getBeanNames();

        //then
        for (String beanName : beanNames) {
            assertTrue(beanNamesForValidation.contains(beanName));
        }
    }

    @Test
    public void testGetBeanById() {
        //prepare
        applicationContext = new ClassPathApplicationContext(pathToContextFile);

        //when
        MailService beanMailService = (MailService) applicationContext.getBean("mailService");
        UserService beanUserService = (UserService) applicationContext.getBean("userService");
        PaymentService beanPaymentService = (PaymentService) applicationContext.getBean("paymentService");

        //then
        assertEquals("com.boroday.ioc.service.MailService", beanMailService.getClass().getName());
        assertEquals("com.boroday.ioc.service.UserService", beanUserService.getClass().getName());
        assertEquals("com.boroday.ioc.service.PaymentService", beanPaymentService.getClass().getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBeanByIdMultipleIdsException() {
        //prepare
        applicationContext = new ClassPathApplicationContext(new String[]{"src/test/resources/contextErrorMultipleBeanIds.xml"});

        //then
        applicationContext.getBean("mailService");
    }

    @Test
    public void testGetBeanByClassSingleBean() {
        //prepare
        applicationContext = new ClassPathApplicationContext(pathToContextFile);

        //when
        MailService beanMailService = applicationContext.getBean(MailService.class);
        UserService beanUserService = applicationContext.getBean(UserService.class);

        //then
        assertEquals("com.boroday.ioc.service.MailService", beanMailService.getClass().getName());
        assertEquals("com.boroday.ioc.service.UserService", beanUserService.getClass().getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBeanByClassMultipleBeansException() {
        //prepare
        applicationContext = new ClassPathApplicationContext(pathToContextFile);

        //then
        applicationContext.getBean(PaymentService.class);
    }

    @Test
    public void testGetBeanByIdAndClass() {
        //prepare
        applicationContext = new ClassPathApplicationContext(pathToContextFile);

        //when
        MailService beanMailService = (MailService) applicationContext.getBean("mailService", MailService.class);
        UserService beanUserService = (UserService) applicationContext.getBean("userService", UserService.class);
        PaymentService beanPaymentService = (PaymentService) applicationContext.getBean("paymentService", PaymentService.class);
        PaymentService beanPaymentWithMaxService = (PaymentService) applicationContext.getBean("paymentWithMaxService", PaymentService.class);

        //then
        assertEquals("com.boroday.ioc.service.MailService", beanMailService.getClass().getName());
        assertEquals("com.boroday.ioc.service.UserService", beanUserService.getClass().getName());
        assertEquals("com.boroday.ioc.service.PaymentService", beanPaymentService.getClass().getName());
        assertEquals("com.boroday.ioc.service.PaymentService", beanPaymentWithMaxService.getClass().getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetBeanByIdAndClassMultipleIdsException() {
        //prepare
        applicationContext = new ClassPathApplicationContext(new String[]{"src/test/resources/contextErrorMultipleBeanIds.xml"});

        //then
        applicationContext.getBean("mailService", MailService.class);
    }
}
