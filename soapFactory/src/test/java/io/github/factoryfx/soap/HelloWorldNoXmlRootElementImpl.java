package io.github.factoryfx.soap;

import io.github.factoryfx.soap.example.SoapDummyRequestException1;
import io.github.factoryfx.soap.example.SoapDummyRequestException2;
import io.github.factoryfx.soap.examplenoroot.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorldNoXmlRootElementImpl implements HelloWorldNoXmlRootElement {
    @Override
    public SoapDummyResponseNoRoot subDummyRequest(SoapDummyRequestNoRoot parameters) throws SoapDummyRequestException1, SoapDummyRequestException2 {
        return new SoapDummyResponseNoRoot();
    }

    @Override
    public SoapDummyResponseNoRoot2 methodWithRequestResponse(SoapDummyRequestNoRoot2 parameters, HttpServletRequest request, HttpServletResponse resp) {
        return null;
    }
}
