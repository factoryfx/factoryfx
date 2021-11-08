package io.github.factoryfx.soap.example;

import jakarta.xml.ws.WebFault;

@WebFault(name = "SomeFaultElement", targetNamespace = "someNS")
public class SoapDummyRequestException1 extends Exception {


    public SoapDummyRequestExceptionFault eCRMInvalidInputElement = new SoapDummyRequestExceptionFault();



    public SoapDummyRequestExceptionFault getFaultInfo() {
        return this.eCRMInvalidInputElement;
    }
}
