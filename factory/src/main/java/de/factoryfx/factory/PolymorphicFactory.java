package de.factoryfx.factory;

public interface PolymorphicFactory<L> {
    /**class of the LiveObject, probably the common interface<br>
     * workaround for java type erasure */
    Class<L> getLiveObjectClass();

}
