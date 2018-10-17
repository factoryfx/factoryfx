package de.factoryfx.soap;

import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.jetty.JettyServer;
import de.factoryfx.server.Microservice;
import de.factoryfx.soap.example.*;
import de.factoryfx.soap.server.SoapJettyServerFactory;
import de.factoryfx.jetty.HttpServerConnectorFactory;
import de.factoryfx.server.MicroserviceBuilder;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.util.stream.Stream;

public class SoapTest {


    @Test
    public void test(){

        SoapHandlerFactory<HelloWorld, Void, SoapJettyServerFactory> soapHandlerFactory = new SoapHandlerFactory<>();
        HelloWorldFactory helloWorldFactory = new HelloWorldFactory();
        helloWorldFactory.service.set(req->new SoapDummyResponse());
        soapHandlerFactory.serviceBean.set(helloWorldFactory);

        SoapJettyServerFactory root = new SoapJettyServerFactory();
        root.soapHandler.set(soapHandlerFactory);
        HttpServerConnectorFactory<Void, SoapJettyServerFactory> httpServerConnectorFactory = new HttpServerConnectorFactory<>();
        httpServerConnectorFactory.host.set("localhost");
        httpServerConnectorFactory.port.set(8088);
        root.connectors.add(httpServerConnectorFactory);

        Microservice<Void, JettyServer, SoapJettyServerFactory, Object> microService = MicroserviceBuilder.buildInMemoryMicroservice(root);
        microService.start();

        callSoapWebService("http://localhost:8088","action");
        DataAndNewMetadata<SoapJettyServerFactory> newFactory = microService.prepareNewFactory();
        helloWorldFactory = new HelloWorldFactory();
        helloWorldFactory.service.set(req->{
            throw new SoapDummyRequestException1();
        });
        newFactory.root.soapHandler.get().serviceBean.set(helloWorldFactory);
        microService.updateCurrentFactory(newFactory,"","",x->true);
        callSoapWebService("http://localhost:8088","action");

    }

    // SAAJ - SOAP Client Testing
    public static void main(String args[]) {
        /*
            The example below requests from the Web Service at:
             http://www.webservicex.net/uszip.asmx?op=GetInfoByCity


            To call other WS, change the parameters below, which are:
             - the SOAP Endpoint URL (that is, where the service is responding from)
             - the SOAP Action

            Also change the contents of the method createSoapEnvelope() in this class. It constructs
             the inner part of the SOAP envelope that is actually sent.
         */
        String soapEndpointUrl = "http://www.webservicex.net/uszip.asmx";
        String soapAction = "http://www.webserviceX.NET/GetInfoByCity";

        callSoapWebService(soapEndpointUrl, soapAction);
    }

    private static void callSoapWebService(String soapEndpointUrl, String soapAction) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);

            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            System.out.println();

            soapConnection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespace = "myNamespace";
        String myNamespaceURI = "http://www.webserviceX.NET";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

            /*
            Constructed SOAP Request Message:
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:myNamespace="http://www.webserviceX.NET">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <myNamespace:GetInfoByCity>
                        <myNamespace:USCity>New York</myNamespace:USCity>
                    </myNamespace:GetInfoByCity>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
            */

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SoapDummyRequest req = new SoapDummyRequest();
        req.dummy = "BLA";
        req.soapDummyRequestNested = new SoapDummyRequestNested();
        req.soapDummyRequestNested.dummy = "BLUB";
        Marshaller marshaller = null;
        DocumentBuilder db = null;
        try {
            marshaller = JAXBContext.newInstance(SoapDummyRequest.class).createMarshaller();
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document document = db.newDocument();
            marshaller.marshal(req, document);
            org.w3c.dom.Node importedNode = soapBody.getOwnerDocument().importNode(document.getChildNodes().item(0), true);
            soapBody.appendChild(importedNode);
        } catch (JAXBException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

    }

    private static SOAPMessage createSOAPRequest(String soapAction) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(soapMessage);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

}
