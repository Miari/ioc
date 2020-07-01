package com.boroday.ioc.context;

import com.boroday.ioc.entity.BeanDefinition;
import com.boroday.ioc.reader.BeanDefinitionReader;
import com.boroday.ioc.reader.XMLBeanDefinitionReader;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class XMLBeanDefinitionReaderTest {
    BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader(new String[]{"src/test/resources/context.xml", "src/test/resources/email-context.xml"});

    @Test
    public void testReadBeanDefinitions() {

        //when
        List<BeanDefinition> beanDefinitions = beanDefinitionReader.readBeanDefinitions();

        //then
        assertEquals(5, beanDefinitions.size());
        //dependencies
        assertEquals("mailService", beanDefinitions.get(0).getId());
        assertEquals("com.boroday.ioc.service.MailService", beanDefinitions.get(0).getBeanClassName());
        assertEquals("POP3", beanDefinitions.get(0).getDependencies().get("protocol"));
        assertEquals("3000", beanDefinitions.get(0).getDependencies().get("port"));
        //refDependencies
        assertEquals("userService", beanDefinitions.get(1).getId());
        assertEquals("com.boroday.ioc.service.UserService", beanDefinitions.get(1).getBeanClassName());
        assertEquals("mailService", beanDefinitions.get(1).getRefDependencies().get("mailService"));
    }
}
