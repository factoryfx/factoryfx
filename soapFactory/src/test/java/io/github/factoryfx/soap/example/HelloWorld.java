package io.github.factoryfx.soap.example;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use= SOAPBinding.Use.LITERAL) //optional
public interface HelloWorld{


    @WebMethod
    public SoapDummyResponse subIMMEDIATETEMPLATECHANGE(
            //@WebParam(partName = "", name = "", targetNamespace = "")
            SoapDummyRequest parameters
    ) throws SoapDummyRequestException1, SoapDummyRequestException2;

    public SoapDummyResponse methodWithRequestResponse(
            SoapDummyRequest2 parameters, HttpServletRequest request, HttpServletResponse resp
    );

}
