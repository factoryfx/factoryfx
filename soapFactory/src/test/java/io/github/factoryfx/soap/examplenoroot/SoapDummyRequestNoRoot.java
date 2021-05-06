package io.github.factoryfx.soap.examplenoroot;

import io.github.factoryfx.soap.example.SoapDummyRequestNested;

import javax.xml.bind.annotation.XmlElement;


public class SoapDummyRequestNoRoot {
    @XmlElement
    public String dummy;

    @XmlElement
    public SoapDummyRequestNested soapDummyRequestNested;

}
