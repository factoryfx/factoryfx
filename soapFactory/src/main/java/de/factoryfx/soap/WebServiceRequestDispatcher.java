package de.factoryfx.soap;

//import javax.xml.ws.WebFault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
        import java.util.HashMap;

public class WebServiceRequestDispatcher {
    private final Object webService;
    private final HashMap<Class,Method> dispatchMap = new HashMap<>();

    public WebServiceRequestDispatcher(Object webService){
        this.webService=webService;

        for (Method m : webService.getClass().getDeclaredMethods()) {
            if (m.getParameterCount() == 1 && m.getReturnType() != null) {
                dispatchMap.put(m.getParameterTypes()[0], m);
            }
        }
    }

    public Object execute(Object request) {
        Method method = dispatchMap.get(request.getClass());
        try {
            return method.invoke(webService, request);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

//            Throwable targetException = ex.getTargetException();
//            fault = Optional.ofNullable(targetException.getClass().getAnnotation(WebFault.class));
//            if (fault.isPresent()) {
//                try {
//                    Method getFaultInfo = targetException.getClass().getMethod("getFaultInfo");
//                    result = getFaultInfo.invoke(targetException);
//                    QName faultCode = new QName(fault.get().targetNamespace(), fault.get().name());
//                    boolean isNil = result == null;
//                    result = new JAXBElement(faultCode,getFaultInfo.getReturnType(),result);
//                    ((JAXBElement)result).setNil(isNil);
//                } catch (InvocationTargetException | NoSuchMethodException e) {
//                    throw new RuntimeException(e);
//                }
//            } else {
//                if (targetException instanceof RuntimeException) {
//                    throw (RuntimeException)targetException;
//                } else {
//                    throw new RuntimeException(targetException);
//                }
//            }
//        }
//    }
}
