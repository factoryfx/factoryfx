package io.github.factoryfx.soap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class WebServiceRequestDispatcher {
    private final Object webService;
    private final HashMap<Class,Invoker> dispatchMap = new HashMap<>();

    private interface ParameterSupplyer {

        Object supply(Object request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    }

    static final ParameterSupplyer HTTP_REQUEST_SUPPLIER = (req, httpReq, httpResp) -> httpReq;
    static final ParameterSupplyer HTTP_RESPONSE_SUPPLIER = (req, httpReq, httpResp) -> httpResp;
    static final ParameterSupplyer REQUEST_SUPPLIER = (req, httpReq, httpResp) -> req;


    private static class Invoker {

        final Method method;
        final ParameterSupplyer[] suppliers;

        private Invoker(Method method, ParameterSupplyer[] suppliers) {
            this.method = method;
            this.suppliers = suppliers;
        }

        public Object invoke(Object webService, Object request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws InvocationTargetException, IllegalAccessException {
            Object[] args = new Object[suppliers.length];
            for (int i = 0; i < args.length; ++i) {
                args[i] = suppliers[i].supply(request,httpServletRequest,httpServletResponse);
            }
            return method.invoke(webService,args);
        }
    }

    public WebServiceRequestDispatcher(Object webService){
        this.webService=webService;

        for (Method m : webService.getClass().getDeclaredMethods()) {
            if (m.getParameterCount() >= 1 && m.getReturnType() != null) {
                ParameterSupplyer[] suppliers = new ParameterSupplyer[m.getParameterCount()];
                boolean isWebService = true;
                Class<?> rootElementClass = null;
                for (int i = 0; i < suppliers.length; ++i) {
                    if (m.getParameterTypes()[i].isAssignableFrom(HttpServletRequest.class)) {
                        suppliers[i] = HTTP_REQUEST_SUPPLIER;
                    } else if (m.getParameterTypes()[i].isAssignableFrom(HttpServletResponse.class)) {
                        suppliers[i] = HTTP_RESPONSE_SUPPLIER;
                    } else if (m.getParameterTypes()[i].getAnnotation(XmlRootElement.class) != null) {
                        if (rootElementClass != null)
                            isWebService = false;
                        suppliers[i] = REQUEST_SUPPLIER;
                        rootElementClass = m.getParameterTypes()[i];
                    } else {
                        isWebService = false;
                    }
                }

                dispatchMap.put(rootElementClass, new Invoker(m,suppliers));
            }
        }
    }

    public WebServiceCallResult execute(Object request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Invoker method = dispatchMap.get(request.getClass());
        try {
            return WebServiceCallResult.fromResult(method.invoke(webService, request, httpServletRequest, httpServletResponse));
        } catch (IllegalAccessException  e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException t) {
            Throwable ex = t.getTargetException();
            if (ex instanceof Error)
                throw (Error)ex;
            if (ex instanceof RuntimeException)
                throw (RuntimeException)ex;
            return WebServiceCallResult.fromFault((Exception)ex);
        }
    }

}
