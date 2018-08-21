package de.factoryfx.soap.example;

import javax.xml.ws.WebFault;

@WebFault(name = "SoapDummyRequestException1", targetNamespace = "")
public class SoapDummyRequestException1 extends Exception{


    private SoapDummyRequestExceptionFault eCRMInvalidInputElement;



    public SoapDummyRequestExceptionFault getFaultInfo() {
        return this.eCRMInvalidInputElement;
    }
}
