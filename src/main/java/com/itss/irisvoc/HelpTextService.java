package com.itss.irisvoc;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class HelpTextService {
    private static final Map<String, String> elements = new HashMap<>();

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XMLHandler handler = new XMLHandler();
//        try {
            parser.parse(new File("C:\\Users\\Kirill\\Desktop\\test.xml"), handler);
//        } catch (SAXParseException ignore) {}

        for (Map.Entry<String, String> entry : elements.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    private static class XMLHandler extends DefaultHandler {
        private String lastElementName, field;
        private StringBuilder desc = new StringBuilder();
        private boolean isDescElement;
        private final LinkedList<String> descContent = new LinkedList<>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            lastElementName = qName;

            // enter to desc tag
            if (lastElementName.equals("desc")) {
                isDescElement = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            // when present 'field' but not present 'desc'
            if (lastElementName.equals("field") && !qName.equals("field") && !qName.equals("desc")) {
                elements.put(field, "");
                field = null;
            }

            if ((field != null && !field.isEmpty()) && qName.equals("desc")) {
                while (descContent.size() != 0) {
                    desc.append(descContent.removeFirst());
                }

                elements.put(field, desc.toString());

                // clear for new element
                field = null;
                desc = new StringBuilder();

                // exit from desc tag
                isDescElement = false;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            String information = new String(ch, start, length);

            information = information.replaceAll("\n", "").trim();

            if (!information.isEmpty()) {
                if (lastElementName.equals("field")) {
                    field = information;
                }
            }

            if (isDescElement) {
                // collect all information between desc tag (include another tags)
                descContent.add(information);
            }
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            super.fatalError(e);
        }
    }

}