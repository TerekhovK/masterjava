package ru.javaops.masterjava;



import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import ru.javaops.masterjava.xml.util.XsltProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: gkislin
 * Date: 05.08.2015
 *
 * @link http://caloriesmng.herokuapp.com/
 * @link https://github.com/JavaOPs/topjava
 */
public class Main {
    public static void main(String[] args) {
        useJAXB("masterJava").forEach(i -> System.out.println(i.getFullName()));
        System.out.print("\n");
        useJAXB("topJava").forEach(i -> System.out.println(i.getFullName()));
        System.out.print("\n");

        useStax("masterJava").forEach(System.out::println);
        System.out.print("\n");
        useStax("topJava").forEach(System.out::println);

        xsltUserTransform();
        xsltProjectTransform("masterJava");
    }

    public static Set<String> useStax(String expectedString) {
        List<String> list = new ArrayList<>();
        Set<String> user = new HashSet<>();
        try (StaxStreamProcessor staxStreamProcessor
                     = new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {

            while (staxStreamProcessor.getReader().hasNext()) {
                XMLStreamReader reader = staxStreamProcessor.getReader();
                int xmlEvent = reader.next();
                if (xmlEvent == XMLEvent.START_ELEMENT) {
                    if (reader.getName().getLocalPart().equals("Project")) {
                        if (reader.getAttributeValue(0).equals(expectedString)) {
                            do {
                                xmlEvent = reader.next();
                                if (xmlEvent == XMLEvent.START_ELEMENT && reader.getName().getLocalPart().equals("Group"))
                                    list.add(reader.getAttributeValue(null, "groupId"));
                            }
                            while (!(xmlEvent == XMLEvent.END_ELEMENT && reader.getName().getLocalPart().equals("Project")));
                        }
                    }
                    if (reader.getName().getLocalPart().equals("User")) {
                        String email = reader.getAttributeValue(null, "email");
                        staxStreamProcessor.doUntil(XMLEvent.START_ELEMENT, "group-ref");
                        String[] split = reader.getElementText().split(" ");
                        staxStreamProcessor.doUntil(XMLEvent.START_ELEMENT, "fullName");
                        String name = reader.getElementText();
                        for (String a : split) {
                            if (list.contains(a)) {
                                user.add(name + "\\" + email);
                            }
                        }
                    }
                }
            }
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }

        return user;
    }


    public static List<User> useJAXB(String expectedString) {
        JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
        jaxbParser.setSchema(Schemas.ofClasspath("payload.xsd"));
        Payload payload = null;
        List<User> userList = null;


        try (InputStream stream = Resources.getResource("payload.xml").openStream()) {
            payload = jaxbParser.unmarshal(stream);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
        if (payload != null) {
            List<Project.Group> collect = payload.getProjects().getProject().stream()
                    .filter((i) -> i.getIdentifier().equals(expectedString))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("There is no such project: %s", expectedString)))
                    .getGroup();

            userList = payload.getUsers().getUser().stream()
                    .filter(i -> i.getGroupRef().stream().anyMatch(collect::contains))
                    .sorted(Comparator.comparing(User::getFullName))
                    .collect(Collectors.toList());
        }

        return userList;
    }

    public static void xsltProjectTransform(String project)  {
        try (InputStream xslInputStream = Resources.getResource("projects.xsl").openStream();
                 InputStream xmlInputStream = Resources.getResource("payload.xml").openStream()) {

                XsltProcessor processor = new XsltProcessor(xslInputStream);
                processor.setParams("Name",project);
                String html = processor.transform(xmlInputStream);
                Files.write(Paths.get("src\\main\\resources\\projects.html"), html.getBytes());
            }
        catch (IOException  | TransformerException e)
        {
            e.printStackTrace();
        }
    }

    public static void xsltUserTransform()  {
        try (InputStream xslInputStream = Resources.getResource("users.xsl").openStream();
             InputStream xmlInputStream = Resources.getResource("payload.xml").openStream()) {

            XsltProcessor processor = new XsltProcessor(xslInputStream);
            String html = processor.transform(xmlInputStream);
            Files.write(Paths.get("src\\main\\resources\\users.html"), html.getBytes());
        }
        catch (IOException  | TransformerException e)
        {
            e.printStackTrace();
        }
    }
}
