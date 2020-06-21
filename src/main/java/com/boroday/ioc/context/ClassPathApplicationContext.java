package com.boroday.dependencyinjection.context;

import com.boroday.dependencyinjection.entity.Bean;
import com.boroday.dependencyinjection.entity.BeanDefinition;
import com.boroday.dependencyinjection.exception.BeanInstantiationException;
import com.boroday.dependencyinjection.reader.BeanDefinitionReader;
import com.boroday.dependencyinjection.reader.XMLBeanDefinitionReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.Class.forName;

public class ClassPathApplicationContext implements ApplicationContext {
    //private String[] path; //todo: почему path должна быть полем, а не локальной переменной?
    private List<Bean> beans;
    private List<BeanDefinition> beanDefinitions;
    private BeanDefinitionReader reader;

    public ClassPathApplicationContext(String[] path) {
        //path = Arrays.copyOf(pathToContextFile, pathToContextFile.length);
        setBeanDefinitionReader(new XMLBeanDefinitionReader(path));
        beanDefinitions = reader.readBeanDefinitions();
        createBeansFromBeanDefinitions();
        injectDependencies();
        injectRefDependencies();
    }

    private void createBeansFromBeanDefinitions() {
        beans = new ArrayList<>();
        for (BeanDefinition beanDefinition : beanDefinitions) {
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
                throw new BeanInstantiationException("Could not create bean from beanDefinition", e);
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
                            Method getMethod = bean.getValue().getClass().getMethod("get" + objectField.substring(0, 1).toUpperCase() + objectField.substring(1));
                            Method setMethod = bean.getValue().getClass().getMethod("set" + objectField.substring(0, 1).toUpperCase() + objectField.substring(1), getMethod.getReturnType());
                            String returnedType = getMethod.getReturnType().getName();
                            if (returnedType.equals("int")) {
                                setMethod.invoke(bean.getValue(), Integer.valueOf(beanDefinition.getDependencies().get(objectField)));
                            } else if (returnedType.equals("short")) {
                                setMethod.invoke(bean.getValue(), Short.valueOf(beanDefinition.getDependencies().get(objectField)));
                            } else if (returnedType.equals("long")) {
                                setMethod.invoke(bean.getValue(), Long.valueOf(beanDefinition.getDependencies().get(objectField)));
                            } else if (returnedType.equals("float")) {
                                setMethod.invoke(bean.getValue(), Float.valueOf(beanDefinition.getDependencies().get(objectField)));
                            } else if (returnedType.equals("double")) {
                                setMethod.invoke(bean.getValue(), Double.valueOf(beanDefinition.getDependencies().get(objectField)));
                            } else if (returnedType.equals("boolean")) {
                                setMethod.invoke(bean.getValue(), Boolean.valueOf(beanDefinition.getDependencies().get(objectField)));
                            } else if (returnedType.equals("byte")) {
                                setMethod.invoke(bean.getValue(), Byte.valueOf(beanDefinition.getDependencies().get(objectField)));
                            } else if (returnedType.equals("java.lang.String")) {
                                setMethod.invoke(bean.getValue(), beanDefinition.getDependencies().get(objectField));
                            } else {
                                throw new NumberFormatException("Type of field is not a primitive or a String");
                            }
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NumberFormatException e) {
                            throw new BeanInstantiationException("Unable to inject dependencies", e);
                        }
                    }
                }
            }
        }
    }

    private void injectRefDependencies() {
        for (Bean bean : beans) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (bean.getId().equals(beanDefinition.getId())) {
                    Set<String> objectFields = beanDefinition.getRefDependencies().keySet();
                    for (String objectField : objectFields) {
                        try {
                            Object refObject = getBean(objectField);
                            Method method = bean.getValue().getClass().getMethod("set" + objectField.substring(0, 1).toUpperCase() + objectField.substring(1), refObject.getClass());
                            method.invoke(bean.getValue(), refObject);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException("Unable to inject references", e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Object getBean(String idOfBean) {
        for (Bean bean : beans) {
            if (bean.getId().equals(idOfBean)) {
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> nameOfClass) {
        for (Bean bean : beans) {
            if (bean.getValue().getClass().equals(nameOfClass)) {
                return (T) bean.getValue();
            }
        }
        return null;
    }

    @Override //todo: не понимаю, зачем нам этот метод, объясни, плиз
    public <T> T getBean(String nameOfBean, Class<T> nameOfClass) {
        for (Bean bean : beans) {
            if (!beans.isEmpty()) {
                if (bean.getValue().getClass().equals(nameOfClass) && (bean.getId().equals(nameOfBean))) {
                    return (T) bean.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public List<String> getBeanNames() {
        List<String> nameOfBeans = new ArrayList<>();
        if (!beans.isEmpty()) {
                for (Bean bean : beans) {
                nameOfBeans.add(bean.getId()); //nameOfBeans.add(bean.getValue().getClass().getName());
            }
        }
        return nameOfBeans;
    }

    @Override
    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        reader = beanDefinitionReader;
    }
}
