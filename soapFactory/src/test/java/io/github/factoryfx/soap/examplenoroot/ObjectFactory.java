package io.github.factoryfx.soap.examplenoroot;

import javax.xml.namespace.QName;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    private final static QName _SoapDummyRequestNoRoot_QNAME = new QName("http://test.namespace", "SoapDummyRequest");
    private final static QName _SoapDummyRequestNoRootResponse_QNAME = new QName("http://test.namespace", "SoapDummyResponse");
    private final static QName _SoapDummyRequestNoRoot2_QNAME = new QName("http://test.namespace", "SoapDummyRequest2");
    private final static QName _SoapDummyRequestNoRootResponse2_QNAME = new QName("http://test.namespace", "SoapDummyResponse2");


    public ObjectFactory() {
    }

    @XmlElementDecl(namespace = "http://test.namespace", name = "SoapDummyRequest")
    public JAXBElement<SoapDummyRequestNoRoot> createSoapDummeRequestNoRoot(SoapDummyRequestNoRoot value) {
        return new JAXBElement<SoapDummyRequestNoRoot>(_SoapDummyRequestNoRoot_QNAME, SoapDummyRequestNoRoot.class, null, value);
    }

    @XmlElementDecl(namespace = "http://test.namespace", name = "SoapDummyResponse")
    public JAXBElement<SoapDummyResponseNoRoot> createSoapDummeResponseNoRoot(SoapDummyResponseNoRoot value) {
        return new JAXBElement<SoapDummyResponseNoRoot>(_SoapDummyRequestNoRootResponse_QNAME, SoapDummyResponseNoRoot.class, null, value);
    }

    @XmlElementDecl(namespace = "http://test.namespace", name = "SoapDummyRequest2")
    public JAXBElement<SoapDummyRequestNoRoot2> createSoapDummeRequestNoRoot(SoapDummyRequestNoRoot2 value) {
        return new JAXBElement<SoapDummyRequestNoRoot2>(_SoapDummyRequestNoRoot2_QNAME, SoapDummyRequestNoRoot2.class, null, value);
    }

    @XmlElementDecl(namespace = "http://test.namespace", name = "SoapDummyResponse2")
    public JAXBElement<SoapDummyResponseNoRoot2> createSoapDummeResponseNoRoot(SoapDummyResponseNoRoot2 value) {
        return new JAXBElement<SoapDummyResponseNoRoot2>(_SoapDummyRequestNoRootResponse2_QNAME, SoapDummyResponseNoRoot2.class, null, value);
    }
}
