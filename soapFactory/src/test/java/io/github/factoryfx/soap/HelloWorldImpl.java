package io.github.factoryfx.soap;

import io.github.factoryfx.soap.example.*;
import org.junit.jupiter.api.Assertions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorldImpl implements HelloWorld {

    @Override
    public SoapDummyResponse subIMMEDIATETEMPLATECHANGE(SoapDummyRequest parameters) throws SoapDummyRequestException1, SoapDummyRequestException2 {
        return new SoapDummyResponse();
    }

    @Override
    public SoapDummyResponse methodWithRequestResponse(SoapDummyRequest2 parameters, HttpServletRequest request, HttpServletResponse resp) {
        return new SoapDummyResponse();
    }
}
