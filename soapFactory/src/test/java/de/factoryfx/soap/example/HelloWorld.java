package de.factoryfx.soap.example;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use= SOAPBinding.Use.LITERAL) //optional
public interface HelloWorld{


    @WebMethod
    public SoapDummyResponse subIMMEDIATETEMPLATECHANGE(
            //@WebParam(partName = "", name = "", targetNamespace = "")
            SoapDummyRequest parameters
    ) throws SoapDummyRequestException1, SoapDummyRequestException2;

}
