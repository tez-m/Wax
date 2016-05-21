package com.tezhm.wax.internal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Tez-Desktop on 5/17/2016.
 */
public class XmlReader
{
    private Map<String, List<FieldTuple>> classMap = new HashMap<>();

    public void parse(File input) throws Exception
    {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(input);
            doc.getDocumentElement().normalize();


            NodeList classList = doc.getElementsByTagName("class");

            for (int i = 0; i < classList.getLength(); ++i)
            {
                Node nNode = classList.item(i);

                if (nNode.getNodeType() != Node.ELEMENT_NODE)
                {
                    throw new IllegalArgumentException();
                }

                Element classElement = (Element) nNode;
                String className = classElement.getAttribute("name");
                // TODO: check if class exists before adding
                classMap.put(className, new ArrayList<FieldTuple>());

                Node nameNode = classElement.getElementsByTagName("name").item(0);
                Node typeNode = classElement.getElementsByTagName("type").item(0);
                Node factoryNode = classElement.getElementsByTagName("factory").item(0);

                if (nameNode.getNodeType() != Node.ELEMENT_NODE)
                {
                    throw new IllegalArgumentException();
                }

                if (typeNode.getNodeType() != Node.ELEMENT_NODE)
                {
                    throw new IllegalArgumentException();
                }

                if (factoryNode.getNodeType() != Node.ELEMENT_NODE)
                {
                    throw new IllegalArgumentException();
                }

                Element nameElement = (Element) nameNode;
                Element typeElement = (Element) typeNode;
                Element factoryElement = (Element) factoryNode;

                String fieldName = nameElement.getAttribute("value");
                String typeName = typeElement.getAttribute("value");
                String factoryName = factoryElement.getAttribute("value");
                FieldTuple field = new FieldTuple(fieldName, typeName, factoryName);

                classMap.get(className).add(field);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, List<FieldTuple>> getClassMap()
    {
        return this.classMap;
    }
}
