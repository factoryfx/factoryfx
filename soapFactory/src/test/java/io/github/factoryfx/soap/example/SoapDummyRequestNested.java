package io.github.factoryfx.soap.example;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SoapDummyRequestNested {
    @XmlElement
    public String dummy;

}
