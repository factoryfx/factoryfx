package io.github.factoryfx.microservice.test.systemtree;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.ValidationResult;

public class GreetingRelayResourceFactory extends SimpleFactoryBase<GreetingRelayResource, BRoot> {
    public final StringAttribute localGreeting = new StringAttribute().labelText("localGreeting").nullable();
    public final FactoryAttribute<TimeApi, TimeApiClientFactory<BRoot>> remoteTime = new FactoryAttribute<>();
    /** deploy-failure switch for the rollback test, same mechanism as MicroserviceRestIntegrationTest */
    public final StringAttribute poison = new StringAttribute().nullable()
            .serverValidation(value -> new ValidationResult("poison".equals(value), new LanguageText("poisoned configuration rejected")));

    @Override
    protected GreetingRelayResource createImpl() {
        return new GreetingRelayResource(localGreeting.get(), remoteTime.instance());
    }
}
