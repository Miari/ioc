package com.boroday.dependencyinjection.context;

import com.boroday.dependencyinjection.entity.Bean;
import com.boroday.dependencyinjection.entity.BeanDefinition;
import com.boroday.dependencyinjection.reader.BeanDefinitionReader;
import com.boroday.dependencyinjection.reader.XMLBeanDefinitionReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.lang.Class.forName;

public class ClassPathApplicationContext implements ApplicationContext {
    private String[] path;
    private List<Bean> beans;
    private List<BeanDefinition> beanDefinitions;
    private BeanDefinitionReader reader;

    public ClassPathApplicationContext(String[] pathToContextFile) {
        path = Arrays.copyOf(pathToContextFile, pathToContextFile.length);
        reader = new XMLBeanDefinitionReader(path); //todo: rework
        beanDefinitions = reader.readBeanDefinitions();
        createBeansFromBeanDefinitions();
        injectDependencies();
    }

    private void createBeansFromBeanDefinitions() {
        beans = new ArrayList<>();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            System.out.println(beanDefinition.toString());
            Bean bean = new Bean();
            bean.setId(beanDefinition.getId());
            String className = beanDefinition.getBeanClassName();
            try {
                Class<?> clazz = forName(className);
                Constructor<?> constructor = clazz.getConstructor();
                Object object = constructor.newInstance();
                bean.setValue(object);
                beans.add(bean);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException("Desired class was not found", e);
            }
        }
    }

    private void injectDependencies() {
        for (Bean bean : beans) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (bean.getId().equals(beanDefinition.getId())) {
                    Set<String> objectFields = beanDefinition.getDependencies().keySet();
                    for (String objectField : objectFields) {
                        try {
                            Method method = bean.getValue().getClass().getMethod("get" + objectField.substring(0, 1).toUpperCase() + objectField.substring(1));
                            method.invoke(bean.getValue());
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace(); //todo
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public Bean getBean(String nameOfBean) {
        return null;
    }

    @Override
    public List<String> getBeanNames() {
        List<String> nameOfBeans = null;
        /*for (Bean bean: beans){
            nameOfBeans.add(bean.getId());
        }*/
        return nameOfBeans;
    }

    @Override
    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {

    }
}
