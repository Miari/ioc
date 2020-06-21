package com.boroday.dependencyinjection.context;

import com.boroday.dependencyinjection.reader.BeanDefinitionReader;

import java.util.List;

public interface ApplicationContext {
    Object getBean(String idOfBean);
    List<String> getBeanNames();
    void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader);

    <T> T getBean(Class<T> nameOfClass);
    <T> T getBean(String idOfBean, Class<T> nameOfClass);
}
