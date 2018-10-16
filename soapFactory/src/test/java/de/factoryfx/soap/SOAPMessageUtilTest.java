package de.factoryfx.soap;

import de.factoryfx.soap.example.HelloWorld;
import de.factoryfx.soap.example.SoapDummyRequest;
import org.junit.Test;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

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

}