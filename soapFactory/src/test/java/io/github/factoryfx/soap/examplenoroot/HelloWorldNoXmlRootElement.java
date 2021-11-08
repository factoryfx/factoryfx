package io.github.factoryfx.soap.examplenoroot;

import io.github.factoryfx.soap.example.SoapDummyRequestException1;
import io.github.factoryfx.soap.example.SoapDummyRequestException2;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.soap.SOAPBinding;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@jakarta.jws.WebService
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
