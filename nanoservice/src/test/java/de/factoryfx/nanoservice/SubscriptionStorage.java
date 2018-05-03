package de.factoryfx.nanoservice;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionStorage {

    public List<SubscriptionFactory> getSubscriptions(){
        ArrayList<SubscriptionFactory> subscriptionFactories = new ArrayList<>();
        SubscriptionFactory subscriptionFactory = new SubscriptionFactory();
        subscriptionFactory.example.set("123");
        subscriptionFactories.add(subscriptionFactory);
        return subscriptionFactories;
    }

}
