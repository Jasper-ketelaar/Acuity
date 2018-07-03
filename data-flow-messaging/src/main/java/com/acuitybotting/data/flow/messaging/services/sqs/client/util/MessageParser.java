package com.acuitybotting.data.flow.messaging.services.sqs.client.util;

import com.acuitybotting.data.flow.messaging.services.Message;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Zachary Herridge on 6/19/2018.
 */
public class MessageParser extends DefaultHandler {

    private static final String MESSAGE = "Message";

    private static final String MESSAGE_ID = "MessageId";
    private static final String MESSAGE_RECEIPT_HANDLE = "ReceiptHandle";
    private static final String MESSAGE_MD_5_OF_BODY = "MD5OfBody";
    private static final String MESSAGE_BODY = "Body";

    private static final String MESSAGE_ATTRIBUTE = "Attribute";
    private static final String MESSAGE_ATTRIBUTE_NAME = "Name";
    private static final String MESSAGE_ATTRIBUTE_VALUE = "Value";

    private List<Message> results = new ArrayList<>();

    private StringBuilder valueBuilder = new StringBuilder();

    private Message currentMessage;
    private String currentAttributeName;
    private String currentAttributeValue;

    public static List<Message> parse(String xml) throws Exception {
        SAXParserFactory parserFactor = SAXParserFactory.newInstance();
        SAXParser parser = parserFactor.newSAXParser();
        MessageParser handler = new MessageParser();
        parser.parse(new InputSource(new StringReader(xml)), handler);
        return handler.getResults();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (MESSAGE.equals(qName)) {
            currentMessage = new Message();
            currentMessage.setAttributes(new HashMap<>());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String value = valueBuilder.toString();
        valueBuilder = new StringBuilder();

        switch (qName) {
            case MESSAGE: {
                results.add(currentMessage);
                currentMessage = null;
                break;
            }
            case MESSAGE_ID: {
                currentMessage.setId(value);
                break;
            }
            case MESSAGE_BODY: {
                currentMessage.setBody(value);
                break;
            }
            case MESSAGE_RECEIPT_HANDLE: {
                currentMessage.setDeliveryTag(value);
                break;
            }
            case MESSAGE_ATTRIBUTE_NAME: {
                currentAttributeName = value;
                break;
            }
            case MESSAGE_ATTRIBUTE_VALUE: {
                currentAttributeValue = value;
                break;
            }
            case MESSAGE_ATTRIBUTE: {
                currentMessage.getAttributes().put(currentAttributeName, currentAttributeValue);
                currentAttributeName = null;
                currentAttributeValue = null;
                break;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        valueBuilder.append(String.copyValueOf(ch, start, length).trim());
    }

    public List<Message> getResults() {
        return results;
    }
}
