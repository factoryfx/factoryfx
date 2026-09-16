package io.github.factoryfx.docu.systemtree;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;
import io.github.factoryfx.jetty.builder.JettyServerRootFactory;

public class RelayResourceFactory extends SimpleFactoryBase<RelayResource, JettyServerRootFactory> {
    public final StringAttribute localGreeting = new StringAttribute().labelText("localGreeting").nullable()
            .serverValidation(value -> new ValidationResult(value != null && value.isBlank(), new LanguageText("greeting must not be blank")));
    public final FactoryAttribute<GreetingApi, GreetingApiClientFactory> remoteGreeting = new FactoryAttribute<>();

    @Override
    protected RelayResource createImpl() {
        return new RelayResource(localGreeting.get(), remoteGreeting.instance());
    }
}
