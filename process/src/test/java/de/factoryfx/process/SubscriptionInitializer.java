package de.factoryfx.process;

import java.util.List;

public class SubscriptionInitializer {

    public SubscriptionInitializer(ProcessExecutor<SubscriptionProcess, SubscriptionParameter> processExecutor) {

        SubscriptionParameter parameter = new SubscriptionParameter();
        parameter.value.set(123);
        processExecutor.create(parameter);

    }

}
