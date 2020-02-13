package io.github.factoryfx.soap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SoapHandler implements Servlet {
    //If not setting the system property javax.xml.soap.MessageFactory to
    //com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl
    //there will be useless log warnings for each MessageFactory.newInstance call.
    //Therefore we only do it once to avoid those annoying logs
    private final MessageFactory SOAP11FACTORY;
    private final MessageFactory SOAP12FACTORY;

    private final WebServiceRequestDispatcher dispatcher;
    private final SOAPMessageUtil soapMessageUtil;

    public SoapHandler(WebServiceRequestDispatcher dispatcher, SOAPMessageUtil soapXmlParser) {
        this.dispatcher = dispatcher;
        this.soapMessageUtil = soapXmlParser;
        try {
            this.SOAP11FACTORY = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            this.SOAP12FACTORY = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            MessageFactory messageFactory;
            boolean soap12 = false;
            String header = request.getHeader("Content-Type");
            if (header != null && header.contains(SOAPConstants.SOAP_1_1_CONTENT_TYPE)) {
                messageFactory = SOAP11FACTORY;
            } else {
                //"application/soap+xml"
                messageFactory = SOAP12FACTORY;
                soap12 = true;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document document = builder.parse(request.getInputStream());

            trimWhitespace(document);

            DOMSource domSource = new DOMSource(document);
            SOAPMessage message = messageFactory.createMessage();
            SOAPPart soapPart = message.getSOAPPart();
            soapPart.setContent(domSource);
            message.saveChanges();

            WebServiceCallResult callResult = dispatcher.execute(message, soapMessageUtil.parseRequest(message), request, response);
            SOAPMessage responseMessage;
            if (callResult.result != null) {
                responseMessage = soapMessageUtil.wrapResponse(callResult.result, messageFactory);
            } else if (callResult.fault != null) {
                responseMessage = soapMessageUtil.wrapFault(callResult.createFaultDetail(), callResult.fault.getMessage(), messageFactory, soap12);
            } else {
                responseMessage = messageFactory.createMessage();
            }

            if (responseMessage.saveRequired()) {
                responseMessage.saveChanges();
            }
            response.setStatus(HttpServletResponse.SC_OK);
            putHeaders(responseMessage.getMimeHeaders(), response);

            OutputStream os = response.getOutputStream();
            responseMessage.writeTo(os);
            os.flush();

        } catch (SOAPException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getServletInfo() {
        return "SOAP Handler";
    }

    @Override
    public void destroy() {
    }

    private void putHeaders(MimeHeaders headers, HttpServletResponse res) {

        Iterator<MimeHeader> it = headers.getAllHeaders();
        while (it.hasNext()) {
            MimeHeader header = it.next();

            String[] values = headers.getHeader(header.getName());
            if (values.length == 1) { res.setHeader(header.getName(), header.getValue()); } else {
                StringBuilder concat = new StringBuilder();
                int i = 0;
                while (i < values.length) {
                    if (i != 0) {
                        concat.append(',');
                    }
                    concat.append(values[i++]);
                }
                res.setHeader(header.getName(), concat.toString());
            }
        }
    }

    ServletConfig config;

    @Override
    public void init(ServletConfig config) {
        this.config = config;
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    private static void trimWhitespace(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                child.setTextContent(child.getTextContent().trim());
            }
            trimWhitespace(child);
        }
    }

}
