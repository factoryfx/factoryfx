package io.github.factoryfx.soap;

import io.github.factoryfx.soap.example.*;

public class HelloWorldImpl implements HelloWorld {

    @Override
    public SoapDummyResponse subIMMEDIATETEMPLATECHANGE(SoapDummyRequest parameters) throws SoapDummyRequestException1, SoapDummyRequestException2 {
        return new SoapDummyResponse();
    }
}
