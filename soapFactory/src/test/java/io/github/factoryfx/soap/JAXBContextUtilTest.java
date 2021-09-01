package io.github.factoryfx.soap;

import io.github.factoryfx.soap.example.HelloWorld;
import io.github.factoryfx.soap.example.SoapDummyRequest;
import io.github.factoryfx.soap.example.SoapDummyRequestNested;
import io.github.factoryfx.soap.examplenoroot.HelloWorldNoXmlRootElement;
import io.github.factoryfx.soap.examplenoroot.ObjectFactory;
import io.github.factoryfx.soap.examplenoroot.SoapDummyRequestNoRoot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Set;

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
        JAXBContext jaxbContextForWebService = JAXBSoapUtil.getJAXBContextForWebService(HelloWorld.class).jaxbContext;

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
        JAXBContext jaxbContextForWebService = JAXBSoapUtil.getJAXBContextForWebService(HelloWorldImpl.class).jaxbContext;

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
    public void test_request_class_no_root_element() throws JAXBException {
        JAXBContext jaxbContextForWebService = JAXBSoapUtil.getJAXBContextForWebService(HelloWorldNoXmlRootElementImpl.class).jaxbContext;

        SoapDummyRequestNoRoot request = new SoapDummyRequestNoRoot();
        request.soapDummyRequestNested = new SoapDummyRequestNested();



        Marshaller m = jaxbContextForWebService.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        m.marshal(new ObjectFactory().createSoapDummeRequestNoRoot(request), sw);
        String result = sw.toString();

        System.out.println(result);

    }

    @Test
    public void test_collectObjectFactoryClassSet() throws Exception {
        Set<Class> classSet = JAXBSoapUtil.collectObjectFactoryClassSet(HelloWorldNoXmlRootElement.class);
        Assertions.assertEquals(Set.of(ObjectFactory.class), classSet);
    }

    @Test
    public void test_collectObjectFactoryClassSet_impl() throws Exception {
        Set<Class> classSet = JAXBSoapUtil.collectObjectFactoryClassSet(HelloWorldNoXmlRootElementImpl.class);
        Assertions.assertEquals(Set.of(ObjectFactory.class), classSet);
    }
}