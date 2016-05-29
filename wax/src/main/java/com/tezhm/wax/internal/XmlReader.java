package com.tezhm.wax.internal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Tez-Desktop on 5/17/2016.
 */
public class XmlReader
{
    private Map<String, List<InjectionField>> classMap = new HashMap<>();

    public void parse(File input) throws Exception
    {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(input);
            doc.getDocumentElement().normalize();


            NodeList classList = doc.getElementsByTagName("Class");

            for (int i = 0; i < classList.getLength(); ++i)
            {
                Node classNode = classList.item(i);

                if (classNode.getNodeType() != Node.ELEMENT_NODE)
                {
                    throw new IllegalArgumentException();
                }

                Element classElement = (Element) classNode;
                String className = classElement.getAttribute("name");
                // TODO: check if class exists before adding
                classMap.put(className, new ArrayList<InjectionField>());

                NodeList fieldList = classElement.getElementsByTagName("Field");

                for (int j = 0; j < fieldList.getLength(); ++j)
                {
                    Node fieldNode = fieldList.item(j);

                    if (fieldNode.getNodeType() != Node.ELEMENT_NODE)
                    {
                        throw new IllegalArgumentException();
                    }

                    Element fieldElement = (Element) fieldNode;
                    String fieldName = fieldElement.getAttribute("name");
                    String typeName = fieldElement.getAttribute("type");
                    String factoryName = fieldElement.getAttribute("factory");

                    InjectionField field = new InjectionField(fieldName, typeName, factoryName);
                    classMap.get(className).add(field);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, List<InjectionField>> getClassMap()
    {
        return this.classMap;
    }
}
