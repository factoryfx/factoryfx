package de.factoryfx.factory;

import java.util.Optional;
import java.util.function.Consumer;

public class Lifecycle<T> implements LifecycleNotifier<T> {
    private Optional<Runnable> startAction=Optional.empty();
    private Optional<Runnable> stopAction=Optional.empty();
    private Optional<Consumer<T>> runtimeQueryConsumer=Optional.empty();

    @Override
    public void setStartAction(Runnable startAction) {
        this.startAction = Optional.of(startAction);
    }

    @Override
    public void setStopAction(Runnable stopAction) {
        this.stopAction = Optional.of(stopAction);
    }

    @Override
    public void setRuntimeQueryConsumer(Consumer<T> runtimeQueryConsumer) {
        this.runtimeQueryConsumer = Optional.of(runtimeQueryConsumer);
    }

    public void start(){
        startAction.ifPresent(Runnable::run);
    }
    public void stop(){
        stopAction.ifPresent(Runnable::run);
    }
    /** intent to query runtime state from the application*/
    public void runtimeQuery(T visitor){
        runtimeQueryConsumer.ifPresent((runtimeQueryConsumer)->runtimeQueryConsumer.accept(visitor));
    }
}
