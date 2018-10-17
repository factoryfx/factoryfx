package de.factoryfx.soap;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Predicate;

public class SoapHandlerFactory<S,V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<SoapHandler<S>,V,R> {

    public final FactoryReferenceAttribute<S,FactoryBase<S,V,R>> serviceBean = new FactoryReferenceAttribute<>(null);
    public final FactoryReferenceAttribute<Predicate<HttpServletRequest>,FactoryBase<Predicate<HttpServletRequest>,V,R>> urlTest = new FactoryReferenceAttribute<>(null);

    @Override
    public SoapHandler<S> createImpl() {
        S webService = serviceBean.instance();
        return new SoapHandler<>(new WebServiceRequestDispatcher(webService), new SOAPMessageUtil(JAXBSoapUtil.getJAXBContextForWebService(webService.getClass())), Optional.ofNullable(urlTest.instance()).orElse(s->true));
    }
}


