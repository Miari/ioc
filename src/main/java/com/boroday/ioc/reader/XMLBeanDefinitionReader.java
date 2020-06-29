package com.boroday.ioc.reader;

import com.boroday.ioc.entity.BeanDefinition;
import com.boroday.ioc.exception.BeanInstantiationException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {
    private final String[] path;

    public XMLBeanDefinitionReader(String[] path) {
        this.path = path;
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        if (path.length < 1) {
            throw new IllegalArgumentException("Path to context is not provided");
        }
        XMLInputFactory factory = XMLInputFactory.newInstance();
        List<BeanDefinition> listOfBeansDefinitions = new ArrayList<>();
        for (String pathToFile : path) {
            BeanDefinition beanDefinition = null;
            Map<String, String> valueMap = null;
            Map<String, String> refMap = null;
            try {
                XMLEventReader xmlEventReader = factory.createXMLEventReader(new FileInputStream(pathToFile));
                while (xmlEventReader.hasNext()) {
                    XMLEvent xmlEvent = xmlEventReader.nextEvent();
                    try {
                        if (xmlEvent.isStartElement()) {
                            StartElement startElement = xmlEvent.asStartElement();
                            if ("bean".equalsIgnoreCase(startElement.getName().getLocalPart())) {
                                beanDefinition = new BeanDefinition();
                                valueMap = new HashMap<>();
                                refMap = new HashMap<>();
                                String idAttribute = startElement.getAttributeByName(new QName("id")).getValue();
                                beanDefinition.setId(idAttribute);
                                String classAttribute = startElement.getAttributeByName(new QName("class")).getValue();
                                beanDefinition.setBeanClassName(classAttribute);
                            } else if ("property".equalsIgnoreCase(startElement.getName().getLocalPart())) {
                                String nameAttribute = startElement.getAttributeByName(new QName("name")).getValue();
                                Attribute valueAttribute = startElement.getAttributeByName(new QName("value"));
                                if (valueAttribute != null) {
                                    String valueAttributeValue = valueAttribute.getValue();
                                    valueMap.put(nameAttribute, valueAttributeValue);
                                } else {
                                    String refAttribute = startElement.getAttributeByName(new QName("ref")).getValue();
                                    refMap.put(nameAttribute, refAttribute);
                                }
                                beanDefinition.setDependencies(valueMap);
                                beanDefinition.setRefDependencies(refMap);
                            }
                        }
                    } catch (NullPointerException e) {
                        throw new BeanInstantiationException("Incorrect setup of XML file", e);
                    }
                    if (xmlEvent.isEndElement() && "bean".equalsIgnoreCase(xmlEvent.asEndElement().getName().getLocalPart())) {
                        listOfBeansDefinitions.add(beanDefinition);
                    }
                }
            } catch (XMLStreamException e) {
                throw new RuntimeException("Processing error occurs during the creation of new XMLEventReader", e);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("XML file was not found", e);
            }

        }
        return listOfBeansDefinitions;
    }
}
