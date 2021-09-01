package io.github.factoryfx.soap;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SOAPMessageUtil {
    private final JAXBContext jaxbContext;
    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private final Map<Class<?>, ObjectFactoryInvoker> objectFactoryInvokerMap;

    static final Logger trashMessagesLogger = Logger.getLogger("javax.xml.soap");

    static {
        trashMessagesLogger.setLevel(Level.OFF); //avoid useless not fixable warning
    }


    public SOAPMessageUtil(JAXBContextWrapper jaxbContextWrapper) {
        this(jaxbContextWrapper.jaxbContext, jaxbContextWrapper.objectFactoryInvokerMap);
    }


    public SOAPMessageUtil(JAXBContext jaxbContext, Map<Class<?>, ObjectFactoryInvoker> objectFactoryInvokerMap) {
        this.jaxbContext = jaxbContext;
        this.objectFactoryInvokerMap = objectFactoryInvokerMap;
    }

    public Object parseRequest(SOAPMessage soapMessage) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object unmarshal = unmarshaller.unmarshal(soapMessage.getSOAPBody().getFirstChild());
            if (unmarshal instanceof JAXBElement) {
                return ((JAXBElement<?>) unmarshal).getValue();
            }
            return unmarshal;
        } catch (JAXBException | SOAPException e) {
            throw new RuntimeException(e);
        }

    }

    public Object parseFault(SOAPFault soapFault) {
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

    public Object wrapToJaxbElement(Object o) {

        if (o.getClass().getAnnotation(XmlRootElement.class) != null) {
            return o;
        }

        ObjectFactoryInvoker objectFactoryInvoker = objectFactoryInvokerMap.get(o.getClass());
        if (objectFactoryInvoker == null) {
            throw new RuntimeException("No appropriate objectFactoryInvoker " + o.getClass().getName());
        }

        try {
            return objectFactoryInvoker.invoke(o);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }

    public SOAPMessage wrapRequest(Object request, MessageFactory messageFactory) {
        try {
//            SOAPFactory soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage requestMessage = messageFactory.createMessage();
            request = wrapToJaxbElement(request);

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


    public SOAPMessage wrapResponse(Object response, MessageFactory messageFactory) {
        try {
            SOAPMessage responseMessage = messageFactory.createMessage();
            response = wrapToJaxbElement(response);


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

    public SOAPMessage wrapFault(JAXBElement fault, String faultString, MessageFactory messageFactory, boolean soap12) {
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
            soapFault.setFaultString(faultString != null ? faultString : "Unspecified error");
            soapFault.addDetail();
            QName qname = importedNode.getNamespaceURI() != null ? new QName(importedNode.getNamespaceURI(), importedNode.getLocalName(), importedNode.getPrefix())
                    : new QName(importedNode.getLocalName());
            DetailEntry detailEntry = soapFault.getDetail().addDetailEntry(qname);
            detailEntry.appendChild(importedNode);


            responseMessage.getSOAPBody().appendChild(importedNode);
            return responseMessage;
        } catch (SOAPException | ParserConfigurationException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ObjectFactoryInvoker {
        private final Object objectFactory;
        private final Method method;

        public ObjectFactoryInvoker(Object objectFactory, Method method) {
            this.objectFactory = objectFactory;
            this.method = method;
        }

        public Object invoke(Object objectToWrap) throws InvocationTargetException, IllegalAccessException {
            return this.method.invoke(objectFactory, objectToWrap);
        }
    }

}
