package io.github.factoryfx.soap;

import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

public class WebServiceRequestDispatcher {

    private final Object webService;
    private final HashMap<Class<?>, Invoker> dispatchMap = new HashMap<>();

    static final class RequestContext {
        final HttpServletRequest request;
        final HttpServletResponse response;
        SOAPMessage soapMessage;

        RequestContext(HttpServletRequest request, HttpServletResponse response, SOAPMessage soapMessage) {
            this.request = request;
            this.response = response;
            this.soapMessage = soapMessage;
        }
    }

    private final ThreadLocal<RequestContext> msgContext = new ThreadLocal<>();
    private final WebServiceContext webServiceContext = new WebServiceContext() {
        @Override
        public SOAPMessageContext getMessageContext() {
            return new SOAPMessageContext() {
                @Override
                public SOAPMessage getMessage() {
                    return ctx().soapMessage;
                }

                @Override
                public void setMessage(SOAPMessage message) {
                    ctx().soapMessage = message;
                }

                @Override
                public Object[] getHeaders(QName header, JAXBContext context, boolean allRoles) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Set<String> getRoles() {
                    return Collections.emptySet();
                }

                @Override
                public void setScope(String name, Scope scope) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Scope getScope(String name) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int size() {
                    return 6;
                }

                private RequestContext ctx() {
                    return msgContext.get();
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean containsKey(Object key) {
                    if (key == null)
                        return false;
                    switch (key.toString()) {
                        case HTTP_REQUEST_HEADERS:
                        case HTTP_REQUEST_METHOD:
                        case HTTP_RESPONSE_CODE:
                        case HTTP_RESPONSE_HEADERS:
                        case SERVLET_REQUEST:
                        case SERVLET_RESPONSE:
                            return true;
                    }
                    return false;
                }

                @Override
                public boolean containsValue(Object value) {
                    if (value == ctx().request ||
                            value == ctx().response ||
                            value == ctx().soapMessage)
                        return true;
                    return false;
                }

                @Override
                public Object get(Object key) {
                    if (key == null)
                        return false;
                    switch (key.toString()) {
                        case HTTP_REQUEST_HEADERS:
                            return buildHeaders(ctx().request);
                        case HTTP_REQUEST_METHOD:
                            return ctx().request.getMethod();
                        case HTTP_RESPONSE_CODE:
                            return ctx().response.getStatus();
                        case HTTP_RESPONSE_HEADERS:
                            return buildHeaders(ctx().response);
                        case SERVLET_REQUEST:
                            return ctx().request;
                        case SERVLET_RESPONSE:
                            return ctx().response;
                    }
                    return null;
                }

                @Override
                public Object put(String key, Object value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Object remove(Object key) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void putAll(Map<? extends String, ?> m) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void clear() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Set<String> keySet() {
                    return new HashSet<>(Arrays.asList(HTTP_REQUEST_HEADERS, HTTP_REQUEST_METHOD, HTTP_RESPONSE_CODE, HTTP_RESPONSE_HEADERS, SERVLET_REQUEST, SERVLET_RESPONSE));
                }

                @Override
                public Collection<Object> values() {
                    return Arrays.asList(ctx().soapMessage, buildHeaders(ctx().request), ctx().request.getMethod(), ctx().response.getStatus(), buildHeaders(ctx().response), ctx().request, ctx().response);
                }

                @Override
                public Set<Entry<String, Object>> entrySet() {
                    HashMap<String, Object> entries = new HashMap<>();
                    entries.put(HTTP_REQUEST_HEADERS, ctx().soapMessage);
                    entries.put(HTTP_REQUEST_METHOD, ctx().soapMessage);
                    entries.put(HTTP_RESPONSE_CODE, ctx().soapMessage);
                    entries.put(HTTP_RESPONSE_HEADERS, ctx().soapMessage);
                    entries.put(SERVLET_REQUEST, ctx().soapMessage);
                    entries.put(SERVLET_RESPONSE, ctx().soapMessage);
                    return entries.entrySet();
                }
            };
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public EndpointReference getEndpointReference(Element... referenceParameters) {
            return null;
        }

        @Override
        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
            return null;
        }
    };

    private Map<String, List<String>> buildHeaders(HttpServletRequest request) {
        HashMap<String, List<String>> map = new HashMap<>();
        Enumeration<String> en = request.getHeaderNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            map.put(name, enum2list(request.getHeaders(name)));
        }
        return map;
    }

    private Map<String, List<String>> buildHeaders(HttpServletResponse response) {
        HashMap<String, List<String>> map = new HashMap<>();
        for (String name : response.getHeaderNames()) {
            map.put(name, response.getHeaders(name).stream().collect(Collectors.toList()));
        }
        return map;
    }

    private List<String> enum2list(Enumeration<String> e) {
        ArrayList<String> l = new ArrayList<>();
        while (e.hasMoreElements()) {
            l.add(e.nextElement());
        }
        return l;
    }

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
                args[i] = suppliers[i].supply(request, httpServletRequest, httpServletResponse);
            }
            return method.invoke(webService, args);
        }
    }

    public WebServiceRequestDispatcher(Object webService) {
        this.webService = webService;
        for (Field f : webService.getClass().getDeclaredFields()) {
            if (f.getAnnotation(Resource.class) != null) {
                if (f.getType().isAssignableFrom(WebServiceContext.class)) {
                    f.setAccessible(true);
                    try {
                        f.set(webService, webServiceContext);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        Set<Class<?>> webParamAnnotatedClassSet = collectWebParamAnnotatedParams(webService.getClass());

        for (Method m : webService.getClass().getDeclaredMethods()) {
            if (Modifier.isPublic(m.getModifiers()) && m.getParameterCount() >= 1 && m.getReturnType() != null) {
                ParameterSupplyer[] suppliers = new ParameterSupplyer[m.getParameterCount()];
                boolean isWebService = true;
                Class<?> rootElementClass = null;
                for (int i = 0; i < suppliers.length; ++i) {
                    if (m.getParameterTypes()[i].isAssignableFrom(HttpServletRequest.class)) {
                        suppliers[i] = HTTP_REQUEST_SUPPLIER;
                    } else if (m.getParameterTypes()[i].isAssignableFrom(HttpServletResponse.class)) {
                        suppliers[i] = HTTP_RESPONSE_SUPPLIER;
                    } else if (m.getParameterTypes()[i].getAnnotation(XmlRootElement.class) != null || webParamAnnotatedClassSet.contains(m.getParameterTypes()[i])) {
                        if (rootElementClass != null)
                            isWebService = false;
                        suppliers[i] = REQUEST_SUPPLIER;
                        rootElementClass = m.getParameterTypes()[i];
                    } else {
                        isWebService = false;
                    }
                }

                if (isWebService)
                    dispatchMap.put(rootElementClass, new Invoker(m, suppliers));
            }
        }
    }

    public static void getWebParamAnnotatedSet(Class<?> clazz, Set<Class<?>> classSet) {

        if (clazz.getAnnotation(WebService.class) != null) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(m.getModifiers())) {
                    for (int i = 0; i < m.getParameterCount(); i++) {
                        if (Arrays.stream(m.getParameterAnnotations()[i]).anyMatch(a -> a instanceof WebParam)) {
                            classSet.add(m.getParameterTypes()[i]);
                        }
                    }
                }
            }
        }
    }

    public static Set<Class<?>> collectWebParamAnnotatedParams(Class<?> clazz) {
        Set<Class<?>> collectedClasses = new HashSet<>();

        while (clazz != null) {
            getWebParamAnnotatedSet(clazz, collectedClasses);
            for (var i : clazz.getInterfaces()) {
                getWebParamAnnotatedSet(i, collectedClasses);
            }
            clazz = clazz.getSuperclass();
        }
        return collectedClasses;

    }

    public WebServiceCallResult execute(SOAPMessage message, Object request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Invoker method = dispatchMap.get(request.getClass());
        try {
            msgContext.set(new RequestContext(httpServletRequest, httpServletResponse, message));
            return WebServiceCallResult.fromResult(method.invoke(webService, request, httpServletRequest, httpServletResponse));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException t) {
            Throwable ex = t.getTargetException();
            if (ex instanceof Error)
                throw (Error) ex;
            if (ex instanceof RuntimeException)
                throw (RuntimeException) ex;
            return WebServiceCallResult.fromFault((Exception) ex);
        } finally {
            msgContext.set(null);
        }
    }

}
