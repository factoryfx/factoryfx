package de.factoryfx.factory;

public interface PolymorphicFactory<L> {
    /**
     * class of the liveobject, probably the common interface<br>
     * workaround for java type erasure
     * @return class liveobject*/
    Class<L> getLiveObjectClass();

}
