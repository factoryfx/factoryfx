package io.github.factoryfx.soap;

import io.github.factoryfx.soap.example.HelloWorld;
import io.github.factoryfx.soap.example.SoapDummyRequest;
import io.github.factoryfx.soap.example.SoapDummyRequestExceptionFault;
import io.github.factoryfx.soap.example.SoapDummyRequestNested;
import io.github.factoryfx.soap.examplenoroot.HelloWorldNoXmlRootElement;
import io.github.factoryfx.soap.examplenoroot.SoapDummyRequestNoRoot;
import io.github.factoryfx.soap.examplenoroot.SoapDummyResponseNoRoot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SOAPMessageUtilTest {

    @Test
    public void parseMessage() throws RuntimeException, SOAPException {
        SOAPMessageUtil soapMessageUtil = new SOAPMessageUtil(JAXBSoapUtil.getJAXBContextForWebService(HelloWorld.class));
        SOAPMessage soapMessage = soapMessageUtil.wrapRequest(new SoapDummyRequest(), MessageFactory.newInstance());


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            soapMessage.writeTo(out);
            String strMsg = new String(out.toByteArray());
            System.out.println(strMsg);
        } catch (SOAPException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testFault() throws SOAPException {
        SOAPMessageUtil soapMessageUtil = new SOAPMessageUtil(JAXBSoapUtil.getJAXBContextForWebService(HelloWorld.class));
        JAXBElement<SoapDummyRequestExceptionFault> el = new JAXBElement<>(new QName("SoapDummyRequestExceptionFault"), SoapDummyRequestExceptionFault.class, new SoapDummyRequestExceptionFault());
        SOAPMessage soapMessage = soapMessageUtil.wrapFault(el, "fault", MessageFactory.newInstance(), true);
        if (soapMessage.saveRequired())
            soapMessage.saveChanges();
        Object exception = soapMessageUtil.parseFault(soapMessage.getSOAPBody().getFault());
        Assertions.assertSame(SoapDummyRequestExceptionFault.class, exception.getClass());


    }


    @Test
    public void parseMessageWoXmlRootElement() throws RuntimeException, SOAPException {
        SOAPMessageUtil soapMessageUtil = new SOAPMessageUtil(JAXBSoapUtil.getJAXBContextForWebService(HelloWorldNoXmlRootElement.class));
        SoapDummyRequestNoRoot soapDummyRequestNoRoot = new SoapDummyRequestNoRoot();
        soapDummyRequestNoRoot.dummy = "strValue";
        soapDummyRequestNoRoot.soapDummyRequestNested = new SoapDummyRequestNested();
        testParseMessage(soapMessageUtil, soapDummyRequestNoRoot);
        testParseMessage(soapMessageUtil, new SoapDummyResponseNoRoot());

    }
    @Test
    public void parseMessageWoXmlRootElement_impl() throws RuntimeException, SOAPException {
        SOAPMessageUtil soapMessageUtil = new SOAPMessageUtil(JAXBSoapUtil.getJAXBContextForWebService(HelloWorldNoXmlRootElementImpl.class));
        SoapDummyRequestNoRoot soapDummyRequestNoRoot = new SoapDummyRequestNoRoot();
        soapDummyRequestNoRoot.dummy = "strValue";
        soapDummyRequestNoRoot.soapDummyRequestNested = new SoapDummyRequestNested();
        testParseMessage(soapMessageUtil, soapDummyRequestNoRoot);
        testParseMessage(soapMessageUtil, new SoapDummyResponseNoRoot());

    }

    public void testParseMessage(SOAPMessageUtil soapMessageUtil, Object request) throws SOAPException {
        SOAPMessage soapMessage = soapMessageUtil.wrapRequest(request, MessageFactory.newInstance());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            soapMessage.writeTo(out);
            String strMsg = new String(out.toByteArray());
            System.out.println(strMsg);
        } catch (SOAPException | IOException e) {
            throw new RuntimeException(e);
        }

    }


}