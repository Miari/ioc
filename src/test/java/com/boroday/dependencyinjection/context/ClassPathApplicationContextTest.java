package com.boroday.dependencyinjection.context;

import com.boroday.dependencyinjection.service.MailService;
import com.boroday.dependencyinjection.service.PaymentService;
import com.boroday.dependencyinjection.service.UserService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ClassPathApplicationContextTest {
    String[] pathToContextFile = {"src/main/resources/context.xml"};
    ApplicationContext applicationContext = new ClassPathApplicationContext(pathToContextFile);

    @Test
    public void testGetBeanNames() {
        //prepare
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
        //when
        Object beanMailService = applicationContext.getBean("mailService");
        Object beanUserService = applicationContext.getBean("userService");
        Object beanPaymentService = applicationContext.getBean("paymentService");

        //then
        assertTrue(beanMailService.getClass().getName().equals("com.boroday.dependencyinjection.service.MailService"));
        assertTrue(beanUserService.getClass().getName().equals("com.boroday.dependencyinjection.service.UserService"));
        assertTrue(beanPaymentService.getClass().getName().equals("com.boroday.dependencyinjection.service.PaymentService"));
    }

    @Test
    public void testGetBeanByClass() {
        //prepare
        MailService mailService = new MailService();
        UserService userService = new UserService();
        PaymentService paymentService = new PaymentService();

        //when
        MailService beanMailService = applicationContext.getBean(mailService.getClass());
        UserService beanUserService = applicationContext.getBean(userService.getClass());
        PaymentService beanPaymentService = applicationContext.getBean(paymentService.getClass());

        //then
        assertTrue(beanMailService.getClass().getName().equals("com.boroday.dependencyinjection.service.MailService"));
        assertTrue(beanUserService.getClass().getName().equals("com.boroday.dependencyinjection.service.UserService"));
        assertTrue(beanPaymentService.getClass().getName().equals("com.boroday.dependencyinjection.service.PaymentService"));
    }
}
