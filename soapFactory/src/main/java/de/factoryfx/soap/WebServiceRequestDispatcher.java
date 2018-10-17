package de.factoryfx.soap;

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

    public WebServiceCallResult execute(Object request) {
        Method method = dispatchMap.get(request.getClass());
        try {
            return WebServiceCallResult.fromResult(method.invoke(webService, request));
        } catch (IllegalAccessException  e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException t) {
            Throwable ex = t.getTargetException();
            if (ex instanceof Error)
                throw (Error)ex;
            if (ex instanceof RuntimeException)
                throw (RuntimeException)ex;
            for (Class<?> exceptionType : method.getExceptionTypes()) {
                if (exceptionType.isAssignableFrom(ex.getClass())) {
                    return WebServiceCallResult.fromFault((Exception)ex);
                }
            }
            throw new RuntimeException(ex);
        }
    }

}
