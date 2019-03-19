package io.github.factoryfx.factory;


/*
use {@link  io.github.factoryfx.factory.PolymorphicFactoryBase} as base class if possible
*/
public interface PolymorphicFactory<L> {
    /**
     * class of the liveobject, probably the common interface<br>
     * workaround for java type erasure
     * @return class liveobject*/
    Class<L> getLiveObjectClass();

}
