package com.boroday.dependencyinjection.reader;

import com.boroday.dependencyinjection.entity.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {
    List<BeanDefinition> readBeanDefinitions();
}
