package com.boroday.ioc.reader;

import com.boroday.ioc.entity.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {
    List<BeanDefinition> readBeanDefinitions();
}
