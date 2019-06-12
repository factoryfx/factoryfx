package io.github.factoryfx.soap;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;

public class SoapHandlerFactory<S,R extends FactoryBase<?,R>> extends SimpleFactoryBase<SoapHandler,R> {

    public final FactoryAttribute<R,S,FactoryBase<S,R>> serviceBean = new FactoryAttribute<>();

    @Override
    protected SoapHandler createImpl() {
        S webService = serviceBean.instance();
        return new SoapHandler(new WebServiceRequestDispatcher(webService), new SOAPMessageUtil(JAXBSoapUtil.getJAXBContextForWebService(webService.getClass())));
    }
}


