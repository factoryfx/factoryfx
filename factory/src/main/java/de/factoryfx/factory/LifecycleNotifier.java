package de.factoryfx.factory;

import java.util.function.Consumer;

public interface LifecycleNotifier<T> {
    void setStartAction(Runnable startAction);
    void setStopAction(Runnable stopAction);
    void setRuntimeQueryConsumer(Consumer<T> runtimeQueryConsumer);
}
