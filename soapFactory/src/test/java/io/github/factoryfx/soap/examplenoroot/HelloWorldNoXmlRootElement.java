package io.github.factoryfx.soap.examplenoroot;

import io.github.factoryfx.soap.example.SoapDummyRequestException1;
import io.github.factoryfx.soap.example.SoapDummyRequestException2;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlSeeAlso;

@javax.jws.WebService
@XmlSeeAlso(ObjectFactory.class)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL) //optional
public interface HelloWorldNoXmlRootElement {

    @WebMethod
    public SoapDummyResponseNoRoot subDummyRequest(
            @WebParam
            SoapDummyRequestNoRoot parameters
    ) throws SoapDummyRequestException1, SoapDummyRequestException2;

    public SoapDummyResponseNoRoot2 methodWithRequestResponse(
            @WebParam
            SoapDummyRequestNoRoot2 parameters, HttpServletRequest request, HttpServletResponse resp
    );

//    public String otherMethod();

}
