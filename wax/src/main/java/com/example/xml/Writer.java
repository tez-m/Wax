package com.example.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 */
public class Writer
{
    public void AppendClass(String className, String[] injections)
    {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try
        {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("Class");
            mainRootElement.setAttribute("name", className);
            doc.appendChild(mainRootElement);

            for (String injection : injections)
            {
                Element company = doc.createElement("Inject");
                company.setAttribute("name", injection);
                mainRootElement.appendChild(company);
            }

            // output DOM XML to console
            ToFile(doc);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void ToFile(Document doc) throws IOException, TransformerException
    {
        FileOutputStream fop = null;

        try
        {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            File file = new File("output.xml");
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists())
            {
                file.createNewFile();
            }

            StreamResult console = new StreamResult(fop);
            transformer.transform(source, console);
            fop.flush();
            fop.close();
        }
        finally
        {
            try
            {
                if (fop != null)
                {
                    fop.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
