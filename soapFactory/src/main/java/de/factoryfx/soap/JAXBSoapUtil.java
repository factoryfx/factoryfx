package de.factoryfx.soap;

import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.WebFault;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JAXBSoapUtil {

    private static boolean hasWebServiceAnnotation(Class<?> clazz) {
        while (clazz != null) {
            if (clazz.getDeclaredAnnotation(WebService.class) != null) {
                return true;
            }
            for (Class<?> anInterface : clazz.getInterfaces()) {
                if (anInterface.getDeclaredAnnotation(WebService.class) != null) {
                    return true;
                }
            }
            clazz=clazz.getSuperclass();
        }
        return false;
    }

    public static JAXBContext getJAXBContextForWebService(Class<?> webService) {
        if (!hasWebServiceAnnotation(webService)){
            throw new IllegalArgumentException("no webservice class");
        }
        Set<Class<?>> classes = new HashSet<>();
        collect(webService,classes);
        try {
            Class[] classesToBeBound = classes.stream().filter(clazz -> clazz.getDeclaredAnnotation(XmlRootElement.class) != null).toArray(Class[]::new);
            return JAXBContext.newInstance(classesToBeBound);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }


    private static void collect(Class<?> clazz, Set<Class<?>> classes) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getParameterCount() == 1 && method.getReturnType() != null) {
                if (classes.add(method.getReturnType())) {
                    collect(method.getReturnType(),classes);
                }
                Class<?> parameterType = method.getParameterTypes()[0];
                if (classes.add(parameterType)) {
                    collect(parameterType,classes);
                }


                for (Class<?> exceptionClass : method.getExceptionTypes()) {
                    if (classes.add(exceptionClass)) {
                        collect(exceptionClass,classes);
                    }
                }

            }
        }
    }
}
