package de.factoryfx.nanoservice;


import de.factoryfx.data.attribute.types.StringAttribute;

public class SubscriptionFactory extends NanoserviceRootFactory<PaymentRequest,Subscription,SubscriptionFactory> {
    public final StringAttribute example = new StringAttribute();

    @Override
    public Subscription createImpl() {
        return new Subscription(example.get());
    }
}
