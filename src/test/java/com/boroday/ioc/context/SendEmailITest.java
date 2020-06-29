package com.boroday.ioc.context;

import com.boroday.ioc.service.UserService;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SendEmailITest {

    @Test
    public void testEmailSending() {
        ApplicationContext applicationContext = new ClassPathApplicationContext(new String[] {"src/test/resources/context.xml", "src/test/resources/email-context.xml"});
        UserService userService = applicationContext.getBean(UserService.class);
        assertTrue(userService.sendEmailWithUserCount());
    }
}
