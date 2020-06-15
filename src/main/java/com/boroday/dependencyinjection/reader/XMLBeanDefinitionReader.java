package com.boroday.dependencyinjection.reader;

import com.boroday.dependencyinjection.entity.BeanDefinition;
import com.boroday.dependencyinjection.exception.BeanInstantiationException;
import com.boroday.dependencyinjection.reader.BeanDefinitionReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLBeanDefinitionReader implements BeanDefinitionReader {
    private String[] path;

    public XMLBeanDefinitionReader(String[] path) {
        this.path = path;
    }

    @Override
    public List<BeanDefinition> readBeanDefinitions() {
        List<BeanDefinition> listOfBeansDefinitions = new ArrayList<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(path[0]);
            NodeList listOfBeanElements = document.getElementsByTagName("bean");

            for (int i = 0; i < listOfBeanElements.getLength(); i++) {
                if (listOfBeanElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) listOfBeanElements.item(i);

                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setId(element.getAttribute("id"));
                    beanDefinition.setBeanClassName(element.getAttribute("class"));

                    NodeList listOfChildNodes = element.getChildNodes();
                    Map<String, String> valueMap = new HashMap<>();
                    Map<String, String> refMap = new HashMap<>();
                    for (int j = 0; j < listOfChildNodes.getLength(); j++) {
                        if (listOfChildNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) listOfChildNodes.item(j);
                            if (childElement.getAttribute("value").isEmpty()) {
                                refMap.put(childElement.getAttribute("name"), childElement.getAttribute("ref"));
                            } else {
                                valueMap.put(childElement.getAttribute("name"), childElement.getAttribute("value"));
                            }
                        }
                    }
                    beanDefinition.setDependencies(valueMap);
                    beanDefinition.setRefDependencies(refMap);
                    listOfBeansDefinitions.add(beanDefinition);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException("It is not possible to parse XML file", e); //todo: rework
        }
        return listOfBeansDefinitions;
    }
}
