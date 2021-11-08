package io.github.factoryfx.soap.example;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name= "SomeFaultElement")
@XmlRootElement(name = "SomeFaultElement", namespace = "someNS")
public class SomeFaultElement {
}
