package de.factoryfx.soap;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class SoapHandlerFactory<S,V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<SoapHandler<S>,V,R> {
    public final FactoryReferenceAttribute<S,FactoryBase<S,V,R>> serviceBean = new FactoryReferenceAttribute<>();//TODO class param


    @Override
    public SoapHandler<S> createImpl() {
//        ContextHandler context = new ContextHandler("/");
//        context.setContextPath("/");
//        context.setHandler(new SoapHandler<>(serviceBean.instance()));
        S webService = serviceBean.instance();
        return new SoapHandler<>(new WebServiceRequestDispatcher(webService), new SOAPMessageUtil(JAXBSoapUtil.getJAXBContextForWebService(webService.getClass())));
    }
}


