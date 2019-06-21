package io.github.factoryfx.soap.example;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
