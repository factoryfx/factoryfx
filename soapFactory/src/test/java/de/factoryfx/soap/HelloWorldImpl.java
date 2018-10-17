package de.factoryfx.soap;


import de.factoryfx.soap.example.*;

public class HelloWorldImpl implements HelloWorld {

    @Override
    public SoapDummyResponse subIMMEDIATETEMPLATECHANGE(SoapDummyRequest parameters) throws SoapDummyRequestException1, SoapDummyRequestException2 {
        return new SoapDummyResponse();
    }
}
