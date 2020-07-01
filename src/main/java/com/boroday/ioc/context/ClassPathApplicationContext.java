package com.boroday.ioc.context;

import com.boroday.ioc.entity.Bean;
import com.boroday.ioc.entity.BeanDefinition;
import com.boroday.ioc.exception.BeanInstantiationException;
import com.boroday.ioc.reader.BeanDefinitionReader;
import com.boroday.ioc.reader.XMLBeanDefinitionReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.Class.forName;

public class ClassPathApplicationContext implements ApplicationContext {
    private List<Bean> beans;
    private List<BeanDefinition> beanDefinitions;

    public ClassPathApplicationContext() { //for testing purposes
        beanDefinitions = null;
    }

    public ClassPathApplicationContext(String[] path) {
        BeanDefinitionReader beanDefinitionReader = new XMLBeanDefinitionReader(path);
        beanDefinitions = beanDefinitionReader.readBeanDefinitions();
        createBeans();
    }

    protected void setBeans(List<Bean> beans) { // for testing purposes only
        this.beans = beans;
    }

    protected void setBeanDefinitions(List<BeanDefinition> beanDefinitions) { // for testing purposes only
        this.beanDefinitions = beanDefinitions;
    }

    private void createBeans() {
        createBeansFromBeanDefinitions(beanDefinitions);
        injectDependencies("dependency");
        injectDependencies("refDependency");
    }

    protected List<Bean> createBeansFromBeanDefinitions(List<BeanDefinition> beanDefinitions) {
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
            } catch (ClassNotFoundException e) {
                throw new BeanInstantiationException("Class for \"" + beanDefinition.getBeanClassName() + "\" class definition from beanDefinition does not exist", e);
            } catch (NoSuchMethodException e) {
                throw new BeanInstantiationException("Constructor in class " + beanDefinition.getBeanClassName() + " does not exist", e);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new BeanInstantiationException("It is not possible to create an Instance of " + beanDefinition.getBeanClassName(), e);
            }
        }
        return beans;
    }

    protected void injectDependencies(String dependencyType) {
        for (Bean bean : beans) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (bean.getId().equals(beanDefinition.getId())) {
                    Set<String> objectFields;
                    if ("dependency".equals(dependencyType)) {
                        objectFields = beanDefinition.getDependencies().keySet();
                        injectSimpleDependencies(objectFields, bean, beanDefinition);
                    } else if ("refDependency".equals(dependencyType)) {
                        objectFields = beanDefinition.getRefDependencies().keySet();
                        injectRefDependencies(objectFields, bean, beanDefinition);
                    } else {
                        throw new IllegalArgumentException("Incorrect dependency type. Allowed values are \"dependency\" and \"refDependency\"");
                    }
                }
            }
        }
    }

    protected void injectSimpleDependencies(Set<String> objectFields, Bean bean, BeanDefinition beanDefinition) {

        for (String objectField : objectFields) {
            try {
                Field field = bean.getValue().getClass().getDeclaredField(objectField);
                Method setMethod = bean.getValue().getClass().getMethod("set" + Character.toUpperCase(objectField.charAt(0)) + objectField.substring(1), field.getType());
                Class<?> returnedType = field.getType();
                Object beanObject = bean.getValue();
                if (int.class == returnedType) {
                    setMethod.invoke(beanObject, Integer.valueOf(beanDefinition.getDependencies().get(objectField)));
                } else if (short.class == returnedType) {
                    setMethod.invoke(beanObject, Short.valueOf(beanDefinition.getDependencies().get(objectField)));
                } else if (long.class == returnedType) {
                    setMethod.invoke(beanObject, Long.valueOf(beanDefinition.getDependencies().get(objectField)));
                } else if (float.class == returnedType) {
                    setMethod.invoke(beanObject, Float.valueOf(beanDefinition.getDependencies().get(objectField)));
                } else if (double.class == returnedType) {
                    setMethod.invoke(beanObject, Double.valueOf(beanDefinition.getDependencies().get(objectField)));
                } else if (boolean.class == returnedType) {
                    setMethod.invoke(beanObject, Boolean.valueOf(beanDefinition.getDependencies().get(objectField)));
                } else if (byte.class == returnedType) {
                    setMethod.invoke(beanObject, Byte.valueOf(beanDefinition.getDependencies().get(objectField)));
                } else if (char.class == returnedType) {
                    setMethod.invoke(beanObject, beanDefinition.getDependencies().get(objectField).charAt(0));
                } else if (String.class == returnedType) {
                    setMethod.invoke(beanObject, beanDefinition.getDependencies().get(objectField));
                } else {
                    throw new NumberFormatException("Type of field " + objectField + " in " + bean.getValue().getClass() + " is not a primitive or a String");
                }
            } catch (NoSuchMethodException e) {
                throw new BeanInstantiationException("Setter for " + objectField + " field of " + bean.getValue().getClass() + " is not found", e);
            } catch (NoSuchFieldException e) {
                throw new BeanInstantiationException(objectField + " field of " + bean.getValue().getClass() + " is not found", e);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanInstantiationException("It is not possible to invoke setter for " + objectField + " field of " + bean.getValue().getClass(), e);
            }
        }
    }

    protected void injectRefDependencies(Set<String> objectFields, Bean bean, BeanDefinition beanDefinition) {
        for (String objectField : objectFields) {
            if (getBean(beanDefinition.getRefDependencies().get(objectField)) == null) {
                throw new BeanInstantiationException("There is no class for the value defined in \"ref\" attribute of \"property\" tag for bean " + bean.getId());
            }
            try {
                Object refObject = getBean(beanDefinition.getRefDependencies().get(objectField));
                Method method = bean.getValue().getClass().getMethod("set" + Character.toUpperCase(objectField.charAt(0)) + objectField.substring(1), refObject.getClass());
                method.invoke(bean.getValue(), refObject);
            } catch (NoSuchMethodException e) {
                throw new BeanInstantiationException("Setter for " + objectField + " field of " + bean.getValue().getClass() + " is not found", e);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanInstantiationException("It is not possible to invoke setter for " + objectField + " field of " + bean.getValue().getClass(), e);
            }
        }
    }

    @Override
    public Object getBean(String idOfBean) {
        Object resultBean = null;
        if (!beans.isEmpty()) {
            for (Bean bean : beans) {
                if (bean.getId().equals(idOfBean)) {
                    if (resultBean == null) {
                        resultBean = bean.getValue();
                    } else {
                        throw new IllegalArgumentException("There are more than 1 Bean for id " + idOfBean);
                    }
                }
            }
            if (resultBean == null) {
                throw new IllegalArgumentException("There is no one Bean for class " + idOfBean);
            }
        } else {
            throw new BeanInstantiationException("No beans exist");
        }
        return resultBean;
    }

    @Override
    public <T> T getBean(Class<T> nameOfClass) {
        Object resultBean = null;
        if (!beans.isEmpty()) {
            for (Bean bean : beans) {
                if (bean.getValue().getClass().equals(nameOfClass)) {
                    if (resultBean == null) {
                        resultBean = bean.getValue();
                    } else {
                        throw new IllegalArgumentException("There are more than 1 Bean for class " + nameOfClass);
                    }
                }
            }
            if (resultBean == null) {
                throw new IllegalArgumentException("There is no one Bean for class " + nameOfClass);
            }
        } else {
            throw new BeanInstantiationException("No beans exist");
        }
        return (T) resultBean;
    }

    @Override
    public <T> T getBean(String idOfBean, Class<T> nameOfClass) {
        Object resultBean = null;
        if (!beans.isEmpty()) {
            for (Bean bean : beans) {
                if (bean.getValue().getClass().equals(nameOfClass) && (bean.getId().equals(idOfBean))) {
                    if (resultBean == null) {
                        resultBean = bean.getValue();
                    } else {
                        throw new IllegalArgumentException("There are more than 1 Bean for " + nameOfClass + " and id " + idOfBean);
                    }
                }
            }
            if (resultBean == null) {
                throw new IllegalArgumentException("There is no one Bean for " + nameOfClass + " and id " + idOfBean);
            }
        } else {
            throw new BeanInstantiationException("No beans exist");
        }
        return (T) resultBean;
    }

    @Override
    public List<String> getBeanNames() {
        List<String> nameOfBeans = new ArrayList<>();
        if (!beans.isEmpty()) {
            for (Bean bean : beans) {
                nameOfBeans.add(bean.getId());
            }
        } else {
            throw new BeanInstantiationException("No beans exist");
        }
        return nameOfBeans;
    }
}
