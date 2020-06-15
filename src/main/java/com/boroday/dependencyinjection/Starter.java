package com.boroday.dependencyinjection;

import com.boroday.dependencyinjection.context.ApplicationContext;
import com.boroday.dependencyinjection.context.ClassPathApplicationContext;
import com.boroday.dependencyinjection.entity.Bean;

import java.util.List;

public class Starter {
    public static void main(String[] args) {
        String[] pathToContextFile = {"src/main/resources/context.xml"};
        ApplicationContext applicationContext = new ClassPathApplicationContext(pathToContextFile);
        /*List<String> beanNames = applicationContext.getBeanNames();
        for (String beanName: beanNames) {
            System.out.println(beanName);
        }*/
    }
}
