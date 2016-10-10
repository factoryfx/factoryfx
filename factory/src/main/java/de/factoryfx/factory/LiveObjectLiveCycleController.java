package de.factoryfx.factory;

public interface LiveObjectLiveCycleController<T> {

    public T create();
    public T reCreate(T previousLiveObject);

    public void start();
    public T reStart(T previousLiveObject);
}
