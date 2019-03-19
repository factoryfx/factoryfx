package io.github.factoryfx.soap.example;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SoapDummyRequest {
    @XmlElement
    public String dummy;

    @XmlElement
    public SoapDummyRequestNested soapDummyRequestNested;

}
