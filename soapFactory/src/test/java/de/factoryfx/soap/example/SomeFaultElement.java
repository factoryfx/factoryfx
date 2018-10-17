package de.factoryfx.soap.example;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name= "SomeFaultElement")
@XmlRootElement(name = "SomeFaultElement", namespace = "someNS")
public class SomeFaultElement {
}
