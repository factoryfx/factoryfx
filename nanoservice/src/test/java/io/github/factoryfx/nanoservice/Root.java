package io.github.factoryfx.nanoservice;

public class Root {
    private final SubscriptionStorage subscriptionStorage;

    public Root(SubscriptionStorage subscriptionStorage) {
        this.subscriptionStorage = subscriptionStorage;


        for (SubscriptionFactory subscriptionFactory : subscriptionStorage.getSubscriptions()) {
            Nanoservice<PaymentRequest,Subscription,SubscriptionFactory> nanoService = new Nanoservice<>(subscriptionFactory);
            PaymentRequest paymentRequest = nanoService.run();

            System.out.println(paymentRequest.example);

        }
    }
}
