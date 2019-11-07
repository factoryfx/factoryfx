package io.github.factoryfx.soap;

import io.github.factoryfx.factory.builder.FactoryTemplateId;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.ServletAndPathFactory;
import io.github.factoryfx.jetty.builder.JettyServerBuilder;
import io.github.factoryfx.jetty.builder.SimpleJettyServerBuilder;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.soap.server.SoapJettyServerFactory;
import io.github.factoryfx.soap.example.*;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

public class SoapTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {

        FactoryTreeBuilder<Server, SoapJettyServerFactory> builder = new FactoryTreeBuilder<>(SoapJettyServerFactory.class);
//        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<>()
//                .withHost("localhost").withPort(8088).removeDefaultJerseyServlet()
//                .withServlet("/*",ctx.get(SoapHandlerFactory.class)).build());

        builder.addBuilder(ctx->{
            return new SimpleJettyServerBuilder<Server, SoapJettyServerFactory>()
                    .withHost("localhost").withPort(8088)
                    .withServlet(new FactoryTemplateId<>("soap",null),"/*", ctx.get(SoapHandlerFactory.class));
        });

        builder.addFactory(SoapHandlerFactory.class, Scope.SINGLETON, ctx->{
            SoapHandlerFactory<HelloWorld, SoapJettyServerFactory> soapHandlerFactory = new SoapHandlerFactory<>();
            HelloWorldFactory helloWorldFactory = new HelloWorldFactory();
            HelloWorld goodCase = new HelloWorld() {

                @Resource
                private WebServiceContext webServiceContext;

                @Override
                public SoapDummyResponse subIMMEDIATETEMPLATECHANGE(SoapDummyRequest parameters) throws SoapDummyRequestException1, SoapDummyRequestException2 {
                    return new SoapDummyResponse();
                }

                @Override
                public SoapDummyResponse methodWithRequestResponse(SoapDummyRequest2 parameters, HttpServletRequest request, HttpServletResponse resp) {
                    Assertions.assertNotNull(request);
                    Assertions.assertNotNull(resp);
                    Assertions.assertSame(request,webServiceContext.getMessageContext().get(MessageContext.SERVLET_REQUEST));
                    Assertions.assertSame(resp,webServiceContext.getMessageContext().get(MessageContext.SERVLET_RESPONSE));
                    return new SoapDummyResponse();
                }
            };
            helloWorldFactory.service.set(goodCase);
            soapHandlerFactory.serviceBean.set(helloWorldFactory);
            return soapHandlerFactory;
        });


        Microservice<Server, SoapJettyServerFactory> microService = builder.microservice().build();
        microService.start();

        callSoapWebService("http://localhost:8088",createSOAPRequest("action"));
        callSoapWebService("http://localhost:8088",createSOAPRequest2());
        DataUpdate<SoapJettyServerFactory> newFactory = microService.prepareNewFactory();
        HelloWorldFactory helloWorldFactory = new HelloWorldFactory();
        HelloWorld badCase = new HelloWorld() {

            @Override
            public SoapDummyResponse subIMMEDIATETEMPLATECHANGE(SoapDummyRequest parameters) throws SoapDummyRequestException1, SoapDummyRequestException2 {
                throw new SoapDummyRequestException1();
            }

            @Override
            public SoapDummyResponse methodWithRequestResponse(SoapDummyRequest2 parameters, HttpServletRequest request, HttpServletResponse resp) {
                return new SoapDummyResponse();
            }
        };

        helloWorldFactory.service.set(badCase);
        newFactory.root.server.get().getServlet(SoapHandlerFactory.class).serviceBean.set(helloWorldFactory);
        microService.updateCurrentFactory(newFactory);
        callSoapWebService("http://localhost:8088",createSOAPRequest("action"));

    }

    // SAAJ - SOAP Client Testing
    public static void main(String args[]) throws Exception {
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

        callSoapWebService(soapEndpointUrl, createSOAPRequest(soapAction));
    }

    private static void callSoapWebService(String soapEndpointUrl, SOAPMessage soapMessage) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(soapMessage, soapEndpointUrl);

            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            System.out.println();

            soapConnection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void createSoapEnvelope(Object req, SOAPMessage soapMessage) throws SOAPException {
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
        Marshaller marshaller = null;
        DocumentBuilder db = null;
        try {
            marshaller = JAXBContext.newInstance(req.getClass()).createMarshaller();
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

        SoapDummyRequest req = new SoapDummyRequest();
        req.dummy = "BLA";
        req.soapDummyRequestNested = new SoapDummyRequestNested();
        req.soapDummyRequestNested.dummy = "BLUB";

        createSoapEnvelope(req,soapMessage);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

    private static SOAPMessage createSOAPRequest2() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(new SoapDummyRequest2(), soapMessage);

        MimeHeaders headers = soapMessage.getMimeHeaders();

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

}
