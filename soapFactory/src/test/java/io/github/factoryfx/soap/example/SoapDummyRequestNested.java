package io.github.factoryfx.soap.example;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SoapDummyRequestNested {
    @XmlElement
    public String dummy;

}
