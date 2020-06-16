package com.boroday.dependencyinjection.context;

import com.boroday.dependencyinjection.entity.BeanDefinition;
import com.boroday.dependencyinjection.reader.BeanDefinitionReader;
import com.boroday.dependencyinjection.reader.XMLBeanDefinitionReader;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class XMLBeanDefinitionReaderTest {
    String[] pathToContextFile = {"src/main/resources/context.xml"};
    BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader(pathToContextFile);

    @Test
    public void testReadBeanDefinitions() {
        //prepare
        BeanDefinition beanDefinitionMailService = new BeanDefinition();
        beanDefinitionMailService.setId("mailService");
        beanDefinitionMailService.setBeanClassName("com.boroday.dependencyinjection.service.MailService");
        Map<String, String> mapValueMailService = new HashMap<>();
        mapValueMailService.put("protocol", "POP3");
        mapValueMailService.put("port", "3000");
        beanDefinitionMailService.setDependencies(mapValueMailService);

        //when
        List<BeanDefinition> beanDefinitions = beanDefinitionReader.readBeanDefinitions();

        //then
        assertEquals(beanDefinitionMailService.getId(), beanDefinitions.get(0).getId());
        assertEquals(beanDefinitionMailService.getBeanClassName(), beanDefinitions.get(0).getBeanClassName());
        assertEquals(beanDefinitionMailService.getDependencies().get("protocol"), beanDefinitions.get(0).getDependencies().get("protocol"));
        assertEquals(beanDefinitionMailService.getDependencies().get("port"), beanDefinitions.get(0).getDependencies().get("port"));
    }

}
