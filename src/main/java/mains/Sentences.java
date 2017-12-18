package mains;


import save.SaveToFile;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generate 'cranfield_sentences.txt' from 'cranfield' collection
 */
public class Sentences {


    private static int docid;
    private static String doctitle;
    private static String doctext;

    public static void main() {
        File dirFolder = new File("cranfield");
        File[] listOfFiles = dirFolder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        read(file);
                        StringBuilder text = new StringBuilder(doctitle + " " + doctext);
                        String[] no_new_line = text.toString().split("\n");
                        text = new StringBuilder();
                        for (String nnl : no_new_line)
                            text.append(nnl).append(" ");

                        String[] phrases = text.toString().split("\\s\\.\\s");
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter("cranfield_sentences.txt", true))) {
                            if (docid == 1) {
                                BufferedWriter clear = new BufferedWriter(new FileWriter("cranfield_sentences.txt"));
                                clear.write("");
                            }
                            for (String p : phrases) {
                                bw.append(p.replaceFirst("[\\s]+", ""));
                                bw.append("\n");
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(SaveToFile.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (FileNotFoundException | XMLStreamException ex) {
                        Logger.getLogger(Sentences.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    public static void read(File file)
            throws FileNotFoundException, XMLStreamException {

        doctitle = "";
        doctext = "";
        boolean bdocid, btitle, btext;
        bdocid = btitle = btext = false;

        // Instance of the class which helps on reading tags
        XMLInputFactory factory = XMLInputFactory.newInstance();

        // Initializing the handler to access the tags in the XML file
        XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(file));

        // Checking the availabilty of the next tag
        while (eventReader.hasNext()) {
            // Event is actually the tag . It is of 3 types
            // <name> = StartEvent
            // </name> = EndEvent
            // data between the StartEvent and the EndEvent
            // which is Characters Event
            XMLEvent event = eventReader.nextEvent();

            // This will trigger when the tag is of type <...>
            if (event.isStartElement()) {
                StartElement element = (StartElement) event;

                // Iterator for accessing the metadeta related
                // the tag started.
                Iterator<Attribute> iterator = element.getAttributes();
                while (iterator.hasNext()) {
                    Attribute attribute = iterator.next();
                    QName name = attribute.getName();
                    String value = attribute.getValue();
                    System.out.println(name + " = " + value);
                }

                // Checking which tag needs to be opened for reading.
                // If the tag matches then the boolean of that tag
                // is set to be true.
                if (element.getName().toString().equalsIgnoreCase("docno")) {
                    bdocid = true;
                }
                if (element.getName().toString().equalsIgnoreCase("title")) {
                    btitle = true;
                }
                if (element.getName().toString().equalsIgnoreCase("text")) {
                    btext = true;
                }
            }

            // This will be triggered when the tag is of type </...>
            if (event.isEndElement()) {
                EndElement element = (EndElement) event;

                // Checking which tag needs to be closed after reading.
                // If the tag matches then the boolean of that tag is
                // set to be false.
                if (element.getName().toString().equalsIgnoreCase("docno")) {
                    bdocid = false;
                }
                if (element.getName().toString().equalsIgnoreCase("title")) {
                    btitle = false;
                }
                if (element.getName().toString().equalsIgnoreCase("text")) {
                    btext = false;
                }
            }

            // Triggered when there is data after the tag which is
            // currently opened.
            if (event.isCharacters()) {
                // Depending upon the tag opened the data is retrieved .
                Characters element = (Characters) event;
                if (bdocid) {
                    docid = Integer.parseInt(element.getData().trim());
                }
                if (btitle) {
                    doctitle += element.getData();
                }
                if (btext) {
                    doctext += element.getData();
                }
            }
        }

    }
}
