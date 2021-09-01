package io.github.factoryfx.soap;

import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
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
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    public static Set<Class> collectObjectFactoryClassSet(Class<?> clazz) {
        Set<Class> classSet = new HashSet<>();
        while (clazz != null) {
            XmlSeeAlso xmlSeeAlso = clazz.getDeclaredAnnotation(XmlSeeAlso.class);
            if (xmlSeeAlso != null) {
                Collections.addAll(classSet, xmlSeeAlso.value());
            }

            for (Class c : clazz.getInterfaces()) {
                xmlSeeAlso = (XmlSeeAlso) c.getDeclaredAnnotation(XmlSeeAlso.class);
                if (xmlSeeAlso != null) {
                    Collections.addAll(classSet, xmlSeeAlso.value());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return classSet.stream().filter(c -> c.getDeclaredAnnotation(XmlRegistry.class) != null).collect(Collectors.toSet());
    }

    public static JAXBContextWrapper getJAXBContextForWebService(Class<?> webService) {
        if (!hasWebServiceAnnotation(webService)) {
            throw new IllegalArgumentException("no webservice class");
        }
        Set<Class<?>> classes = new HashSet<>();
        collect(webService, classes);


        Set<Class> objectFactoryClassList = collectObjectFactoryClassSet(webService);
        Map<Class<?>, SOAPMessageUtil.ObjectFactoryInvoker> objectFactoryInvokerMap = new HashMap<>();
        for (Class<?> objectFactoryClass : objectFactoryClassList) {
            classes.add(objectFactoryClass);
            Object objectFactoryInstance;
            try {
                objectFactoryInstance = objectFactoryClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            for (Method factoryMethod : objectFactoryClass.getDeclaredMethods()) {
                if (Modifier.isPublic(factoryMethod.getModifiers())
                        && factoryMethod.isAnnotationPresent(XmlElementDecl.class)
                        && factoryMethod.getReturnType() == JAXBElement.class
                        && factoryMethod.getParameterCount() == 1) {
                    objectFactoryInvokerMap.put(factoryMethod.getParameterTypes()[0], new SOAPMessageUtil.ObjectFactoryInvoker(objectFactoryInstance, factoryMethod));
                }
            }
        }


        try {
            Class[] classesToBeBound = classes.stream().filter(clazz -> clazz.getDeclaredAnnotation(XmlRootElement.class) != null || objectFactoryClassList.contains(clazz)).toArray(Class[]::new);
            return new JAXBContextWrapper(JAXBContext.newInstance(classesToBeBound), objectFactoryInvokerMap);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private static void collect(Class<?> clazz, Set<Class<?>> classes) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getReturnType() != null) {
                if (classes.add(method.getReturnType())) {
                    collect(method.getReturnType(), classes);
                }
                for (int i = 0; i < method.getParameterCount(); ++i) {
                    Class<?> parameterType = method.getParameterTypes()[0];
                    if (classes.add(parameterType)) {
                        collect(parameterType, classes);
                    }
                }


                collectExceptions(classes, method);

            }
        }
    }

    private static void collectExceptions(Set<Class<?>> classes, Method method) {
        for (Class<?> exceptionClass : method.getExceptionTypes()) {
            if (classes.add(exceptionClass)) {
                collect(exceptionClass, classes);
            }
        }
        Class<?> thisClass = method.getDeclaringClass();
        for (Class<?> i : thisClass.getInterfaces()) {
            try {
                Method parentMethod = i.getDeclaredMethod(method.getName(), method.getParameterTypes());
                collectExceptions(classes, parentMethod);
            } catch (NoSuchMethodException ignored) {
            }
        }
        Class<?> parent = thisClass.getSuperclass();
        while (parent != null) {
            try {
                Method parentMethod = parent.getDeclaredMethod(method.getName(), method.getParameterTypes());
                collectExceptions(classes, parentMethod);
                return;
            } catch (NoSuchMethodException ignored) {
            }
            parent = parent.getSuperclass();
        }
    }
}
