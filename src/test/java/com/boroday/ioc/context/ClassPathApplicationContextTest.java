package com.boroday.ioc.context;

import com.boroday.ioc.entity.Bean;
import com.boroday.ioc.entity.BeanDefinition;
import com.boroday.ioc.service.MailService;
import com.boroday.ioc.service.PaymentService;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassPathApplicationContextTest {
    ClassPathApplicationContext classPathApplicationContext;

    Bean beanMail;
    Bean beanPayment;
    Bean beanPaymentWithMax;
    List<Bean> beansForValidation;
    List<Bean> beansForValidationShort;

    BeanDefinition beanDefinitionPayment;
    BeanDefinition beanDefinitionMail;
    BeanDefinition beanDefinitionPaymentWithMax;
    List<BeanDefinition> beanDefinitions;

    @Before
    public void prepareData(){
        beanMail = new Bean();
        beanMail.setId("mailService");
        beanMail.setValue(new MailService());

        beanPayment = new Bean();
        beanPayment.setId("paymentService");
        beanPayment.setValue(new PaymentService());

        beanPaymentWithMax = new Bean();
        beanPaymentWithMax.setId("paymentWithMaxService");
        beanPaymentWithMax.setValue(new PaymentService());

        beansForValidationShort = new ArrayList<>();
        beansForValidationShort.add(beanMail);
        beansForValidationShort.add(beanPayment);

        beansForValidation = new ArrayList<>();
        beansForValidation.add(beanMail);
        beansForValidation.add(beanPayment);
        beansForValidation.add(beanPaymentWithMax);

        beanDefinitionMail = new BeanDefinition();
        beanDefinitionMail.setId("mailService");
        beanDefinitionMail.setBeanClassName("com.boroday.ioc.service.MailService");
        Map<String, String> valueMapMail = new HashMap<>();
        valueMapMail.put("protocol", "IMAP");
        valueMapMail.put("port", "7000");
        beanDefinitionMail.setDependencies(valueMapMail);

        beanDefinitionPayment = new BeanDefinition();
        beanDefinitionPayment.setId("paymentService");
        beanDefinitionPayment.setBeanClassName("com.boroday.ioc.service.PaymentService");
        Map<String, String> refMapPayment = new HashMap<>();
        refMapPayment.put("mailService", "mailService");
        beanDefinitionPayment.setRefDependencies(refMapPayment);

        beanDefinitionPaymentWithMax = new BeanDefinition();
        beanDefinitionPaymentWithMax.setId("paymentWithMaxService");
        beanDefinitionPaymentWithMax.setBeanClassName("com.boroday.ioc.service.PaymentService");
        Map<String, String> valueMapPaymentWithMax = new HashMap<>();
        valueMapPaymentWithMax.put("maxAmount", "7300");
        beanDefinitionPaymentWithMax.setDependencies(valueMapPaymentWithMax);
        Map<String, String> refMapPaymentWithMax = new HashMap<>();
        refMapPaymentWithMax.put("mailService", "mailService");
        beanDefinitionPayment.setRefDependencies(refMapPaymentWithMax);

        beanDefinitions = new LinkedList<>();
        beanDefinitions.add(beanDefinitionMail);
        beanDefinitions.add(beanDefinitionPayment);
        beanDefinitions.add(beanDefinitionPaymentWithMax);
    }

    @Test
    public void testCreateBeansFromBeanDefinitions(){
        //prepare
        classPathApplicationContext = new ClassPathApplicationContext();
        List<Bean> beans = classPathApplicationContext.createBeansFromBeanDefinitions(beanDefinitions);


        //then
        for (Bean bean: beans){
            assertTrue(beansForValidation.contains(bean));
        }
    }

    @Test
    public void testInjectSimpleDependencies() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //prepare
        classPathApplicationContext = new ClassPathApplicationContext();

        Set<String> objectFieldsDependencyMail = new HashSet<>();
        objectFieldsDependencyMail.add("protocol");
        objectFieldsDependencyMail.add("port");

        //when
        classPathApplicationContext.injectSimpleDependencies(objectFieldsDependencyMail, beanMail, beanDefinitionMail);

        //then
        assertEquals("IMAP", beanMail.getValue().getClass().getMethod("getProtocol").invoke(beanMail.getValue()));
        assertEquals(7000, beanMail.getValue().getClass().getMethod("getPort").invoke(beanMail.getValue()));
    }

    @Test
    public void testInjectRefDependencies() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //prepare
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext();
        Set<String> objectFieldsRefDependencyPayment = new HashSet<>();
        objectFieldsRefDependencyPayment.add("mailService");

        classPathApplicationContext.setBeans(beansForValidationShort);

        //when
        classPathApplicationContext.injectRefDependencies(objectFieldsRefDependencyPayment, beanPayment, beanDefinitionPayment);

        //then
        assertEquals(MailService.class, (beanPayment.getValue().getClass().getMethod("getMailService").invoke(beanPayment.getValue())).getClass());
    }
}
