package reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

import interfaces.Tokenizer;
import interfaces.Indexer;
import indexer.WeightIndexer;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 * <p>
 * A class for the Corpus Reader
 */
public class CorpusReader {

    private static int docid;
    private static String doctitle;
    private static String doctext;

    /**
     * Reads files inside a directory while tokenizing the content and indexing the resulting tokens
     *
     * @param dir       the collection directory
     * @param tokenizer the tokenizer object to tokenize terms
     * @param indexer   the indexer object to index terms
     */
    public static void readAndProcessDir(String dir, Tokenizer tokenizer, Indexer indexer) {
        File dirFolder = new File(dir);
        File[] listOfFiles = dirFolder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {

                        read(file);
                        tokenizer.tokenize(doctitle + " " + doctext, "[a-zA-Z]{3,}", true, true);
                        indexer.index(tokenizer.getTokens(), docid);
                        tokenizer.clear();
                    } catch (FileNotFoundException | XMLStreamException ex) {
                        Logger.getLogger(CorpusReader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        if (indexer instanceof WeightIndexer)
            ((WeightIndexer) indexer).weightIndex();
    }

    /**
     * Reads the xml file storing the content from selected tags, in this case: title; text and docID
     *
     * @param file a file
     */
    private static void read(File file)
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
