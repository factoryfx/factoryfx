package io.github.factoryfx.soap;

import io.github.factoryfx.soap.example.HelloWorld;
import io.github.factoryfx.soap.example.SoapDummyRequest;
import io.github.factoryfx.soap.example.SoapDummyRequest2;
import io.github.factoryfx.soap.example.SoapDummyRequestException1;
import io.github.factoryfx.soap.example.SoapDummyRequestException2;
import io.github.factoryfx.soap.example.SoapDummyResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
