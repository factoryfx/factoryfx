package de.factoryfx.factory;

/*
*
* */
public interface LiveCycleController<T,V> {

    /** create and prepare the liveObject*/
    T create();

    /**the factory data has changed therefore a new liveobject is needed.
     * previousLiveObject can be used to reuse resources like conenction pools*/
    default T reCreate(T previousLiveObject) {
        return create();
    }

    /** start the liveObject e.g open a port*/
    default void start(T newLiveObject) { }

    /** free liveObject e.g close a port*/
    default void destroy(T previousLiveObject) {};

    /** execute visitor to get runtime informations from the liveobject*/
    default void runtimeQuery(V visitor, T currentLiveObject) {};
}
