package de.factoryfx.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.WebFault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WebServiceCallResult {

    public final Object result;
    public final Exception fault;

    private WebServiceCallResult(Object result) {
        this.result = result;
        this.fault = null;
    }

    private WebServiceCallResult(Exception ex) {
        this.result = null;
        this.fault = ex;
    }

    public JAXBElement createFaultDetail() {
        try {
            WebFault faultAnnotation = fault.getClass().getAnnotation(WebFault.class);
            if (faultAnnotation == null) {
                throw new RuntimeException("Declared exception of type '"+fault.getClass().getName()+"' does not have an WebFault annotation. Unable to convert it to an xml element thus.");
            }
            Method getFaultInfo = fault.getClass().getMethod("getFaultInfo");
            if (getFaultInfo == null) {
                throw new RuntimeException("Declared exception of type '"+fault.getClass().getName()+"' does not have a getFaultInfo() method. Unable to create a soap fault thus.");
            }
            Object faultInfo = getFaultInfo.invoke(fault);
            QName faultCode = new QName(faultAnnotation.targetNamespace(), faultAnnotation.name());
            JAXBElement result = new JAXBElement(faultCode,getFaultInfo.getReturnType(),faultInfo);
            result.setNil(faultInfo == null);
            return result;
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Cannot convert declared Exception to Webfault. Please check @WebFault annotation",e);
        }

    }

    public static WebServiceCallResult fromResult(Object result) {
        return new WebServiceCallResult(result);
    }

    public static WebServiceCallResult fromFault(Exception fault) {
        return new WebServiceCallResult(fault);
    }
}
