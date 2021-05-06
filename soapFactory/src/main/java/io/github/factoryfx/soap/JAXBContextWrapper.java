package io.github.factoryfx.soap;

import javax.xml.bind.JAXBContext;
import java.util.Map;

public class JAXBContextWrapper {
    public final JAXBContext jaxbContext;
    public final Map<Class<?>, SOAPMessageUtil.ObjectFactoryInvoker> objectFactoryInvokerMap;

    public JAXBContextWrapper(JAXBContext jaxbContext, Map<Class<?>, SOAPMessageUtil.ObjectFactoryInvoker> objectFactoryInvokerMap) {
        this.jaxbContext = jaxbContext;
        this.objectFactoryInvokerMap = objectFactoryInvokerMap;
    }
}
