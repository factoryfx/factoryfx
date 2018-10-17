package de.factoryfx.soap;


import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SOAPMessageUtil {
    private final JAXBContext jaxbContext;
    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public SOAPMessageUtil(JAXBContext jaxbContext) {
        Logger.getLogger("javax.xml.soap").setLevel(Level.OFF); //avoid useless not fixable warning
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
//            if (fault.isPresent()) {
//                SOAPFault soapFault = response.getSOAPBody().addFault();
//                soapFault.setFaultCode(faultCode.orElse(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "Server")));
//                soapFault.setFaultString(faultString.orElse("Unspecified error"));
//                soapFault.addDetail();
//                DetailEntry detailEntry = soapFault.getDetail().addDetailEntry(new QName(importedNode.getNamespaceURI(), importedNode.getLocalName(), importedNode.getPrefix()));
//                detailEntry.appendChild(importedNode);
//
//                ((SoapSupportResource) This).logSoapFault(response.getSOAPBody(), logger);
//
//            } else {
//                response.getSOAPBody().appendChild(importedNode);
//            }


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
            DetailEntry detailEntry = soapFault.getDetail().addDetailEntry(new QName(importedNode.getNamespaceURI(), importedNode.getLocalName(), importedNode.getPrefix()));
            detailEntry.appendChild(importedNode);


            responseMessage.getSOAPBody().appendChild(importedNode);
            return responseMessage;
        } catch (SOAPException | ParserConfigurationException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
