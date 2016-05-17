package com.tezhm.wax.internal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Tez-Desktop on 5/17/2016.
 */
public class XmlReader
{
    public void parse(String xml)
    {
        ArrayList<String> rolev = new ArrayList<String>();
        Document dom;
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the
            // XML file
            dom = db.parse(xml);

            Element doc = dom.getDocumentElement();

            String role1 = getTextValue(role1, doc, "role1");
            if (role1 != null)
            {
                if (!role1.isEmpty())
                {
                    rolev.add(role1);
                }
            }
            String role2 = getTextValue(role2, doc, "role2");
            if (role2 != null)
            {
                if (!role2.isEmpty())
                {
                    rolev.add(role2);
                }
            }
            String role3 = getTextValue(role3, doc, "role3");
            if (role3 != null)
            {
                if (!role3.isEmpty())
                {
                    rolev.add(role3);
                }
            }
            String role4 = getTextValue(role4, doc, "role4");
            if (role4 != null)
            {
                if (!role4.isEmpty())
                {
                    rolev.add(role4);
                }
            }
            return true;

        }
        catch (ParserConfigurationException pce)
        {
            System.out.println(pce.getMessage());
        }
        catch (SAXException se)
        {
            System.out.println(se.getMessage());
        }
        catch (IOException ioe)
        {
            System.err.println(ioe.getMessage());
        }

        return false;
    }


    public String getNextClass()
    {

    }

    public FieldTuple getNextField()
    {

    }

    private String getTextValue(Element doc, String tag)
    {
        String value = def;
        NodeList nl;
        nl = doc.getElementsByTagName(tag);
        if (nl.getLength() > 0 && nl.item(0).hasChildNodes())
        {
            value = nl.item(0).getFirstChild().getNodeValue();
        }
        return value;
    }
}
