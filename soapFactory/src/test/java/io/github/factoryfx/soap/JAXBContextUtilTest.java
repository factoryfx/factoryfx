package io.github.factoryfx.soap;

import io.github.factoryfx.soap.example.HelloWorld;
import io.github.factoryfx.soap.example.SoapDummyRequest;
import io.github.factoryfx.soap.example.SoapDummyRequestNested;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class JAXBContextUtilTest {

    @Test
    public void test_invalid_class(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            JAXBSoapUtil.getJAXBContextForWebService(String.class);
        });
    }

    @Test
    public void test_valid_class(){
        JAXBSoapUtil.getJAXBContextForWebService(HelloWorld.class);
    }

    @Test
    public void test_request_class() throws JAXBException {
        JAXBContext jaxbContextForWebService = JAXBSoapUtil.getJAXBContextForWebService(HelloWorld.class);

        SoapDummyRequest request = new SoapDummyRequest();
        request.soapDummyRequestNested= new SoapDummyRequestNested();



        Marshaller m = jaxbContextForWebService.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        m.marshal(request, sw);
        String result = sw.toString();

        System.out.println(result);

    }


    @Test
    public void test_request_class_impl() throws JAXBException {
        JAXBContext jaxbContextForWebService = JAXBSoapUtil.getJAXBContextForWebService(HelloWorldImpl.class);

        SoapDummyRequest request = new SoapDummyRequest();
        request.soapDummyRequestNested= new SoapDummyRequestNested();



        Marshaller m = jaxbContextForWebService.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        m.marshal(request, sw);
        String result = sw.toString();

        System.out.println(result);

    }
}