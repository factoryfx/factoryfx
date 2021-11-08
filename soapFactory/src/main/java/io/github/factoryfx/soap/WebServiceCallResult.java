package io.github.factoryfx.soap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.ws.WebFault;

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

    @SuppressWarnings("unchecked")
    public JAXBElement<?> createFaultDetail() {
        try {
            WebFault faultAnnotation = fault.getClass().getAnnotation(WebFault.class);
            if (faultAnnotation == null) {
                throw new RuntimeException("Declared exception of type '"+fault.getClass().getName()+"' does not have an WebFault annotation. Unable to convert it to an xml element thus.");
            }

            Method getFaultInfo;
            try {
                getFaultInfo = fault.getClass().getMethod("getFaultInfo");
            } catch ( NoSuchMethodException e){
                throw new RuntimeException("Declared exception of type '"+fault.getClass().getName()+"' does not have a getFaultInfo() method. Unable to create a soap fault thus.",e);
            }
            Object faultInfo = getFaultInfo.invoke(fault);
            QName faultCode = new QName(faultAnnotation.targetNamespace(), faultAnnotation.name());
            JAXBElement result = new JAXBElement(faultCode,getFaultInfo.getReturnType(),faultInfo);
            result.setNil(faultInfo == null);
            return result;
        } catch (InvocationTargetException | IllegalAccessException e) {
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
