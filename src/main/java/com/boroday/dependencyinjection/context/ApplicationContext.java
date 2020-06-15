package com.boroday.dependencyinjection.context;

import com.boroday.dependencyinjection.entity.Bean;
import com.boroday.dependencyinjection.reader.BeanDefinitionReader;

import java.util.List;

public interface ApplicationContext {
    Bean getBean(String nameOfBean);

    //T <> getBean(Class<T> nameOfClass);
    List<String> getBeanNames();

    void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader);
}
