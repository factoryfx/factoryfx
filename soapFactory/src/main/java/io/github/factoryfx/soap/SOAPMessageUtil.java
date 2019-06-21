package io.github.factoryfx.soap;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SOAPMessageUtil {
    private final JAXBContext jaxbContext;
    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    static final Logger trashMessagesLogger = Logger.getLogger("javax.xml.soap");
    static {
        trashMessagesLogger.setLevel(Level.OFF); //avoid useless not fixable warning
    }


    public SOAPMessageUtil(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public Object parseRequest(SOAPMessage soapMessage){
        try {

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return unmarshaller.unmarshal(soapMessage.getSOAPBody().getFirstChild());

        } catch (JAXBException | SOAPException e) {
            throw new RuntimeException(e);
        }

    }

    public Object parseFault(SOAPFault soapFault){
        try {

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object unmarshalledExceptionElement = unmarshaller.unmarshal(soapFault.getDetail().getDetailEntries().next());

            Object exceptionElement;

            if (unmarshalledExceptionElement instanceof JAXBElement) {
                JAXBElement jaxbElement = (JAXBElement) unmarshalledExceptionElement;
                exceptionElement = jaxbElement.getValue();
            } else {
                exceptionElement = unmarshalledExceptionElement;
            }

            return exceptionElement;

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }

    public SOAPMessage wrapRequest(Object request, MessageFactory messageFactory){
        try {
//            SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage requestMessage = messageFactory.createMessage();

            Marshaller marshaller = jaxbContext.createMarshaller();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document document = db.newDocument();
            marshaller.marshal(request, document);
            org.w3c.dom.Node importedNode = requestMessage.getSOAPBody().getOwnerDocument().importNode(document.getChildNodes().item(0), true);
            requestMessage.getSOAPBody().appendChild(importedNode);
            requestMessage.saveChanges();
            return requestMessage;
        } catch (SOAPException | ParserConfigurationException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }


    public SOAPMessage wrapResponse(Object response, MessageFactory messageFactory){
        try {
            SOAPMessage responseMessage = messageFactory.createMessage();

            Marshaller marshaller = jaxbContext.createMarshaller();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document document = db.newDocument();
            marshaller.marshal(response, document);
            org.w3c.dom.Node importedNode = responseMessage.getSOAPBody().getOwnerDocument().importNode(document.getChildNodes().item(0), true);



            responseMessage.getSOAPBody().appendChild(importedNode);
            return responseMessage;
        } catch (SOAPException | ParserConfigurationException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public SOAPMessage wrapFault(JAXBElement fault, String faultString, MessageFactory messageFactory, boolean soap12){
        try {
            SOAPMessage responseMessage = messageFactory.createMessage();

            Marshaller marshaller = jaxbContext.createMarshaller();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document document = db.newDocument();
            marshaller.marshal(fault, document);
            org.w3c.dom.Node importedNode = responseMessage.getSOAPBody().getOwnerDocument().importNode(document.getChildNodes().item(0), true);
            SOAPFault soapFault = responseMessage.getSOAPBody().addFault();
            if (soap12) {
                soapFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
            } else {
                soapFault.setFaultCode(fault.getName());
            }
            soapFault.setFaultString(faultString!=null?faultString:"Unspecified error");
            soapFault.addDetail();
            QName qname = importedNode.getNamespaceURI()!=null?new QName(importedNode.getNamespaceURI(), importedNode.getLocalName(), importedNode.getPrefix())
                    :new QName(importedNode.getLocalName());
            DetailEntry detailEntry = soapFault.getDetail().addDetailEntry(qname);
            detailEntry.appendChild(importedNode);


            responseMessage.getSOAPBody().appendChild(importedNode);
            return responseMessage;
        } catch (SOAPException | ParserConfigurationException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
