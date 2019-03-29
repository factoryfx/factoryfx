package io.github.factoryfx.factory.attribute.dependency;


import io.github.factoryfx.factory.FactoryBase;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryReferenceAttribute<R extends FactoryBase<?,R>,L , F extends FactoryBase<L,R>> extends FactoryReferenceBaseAttribute<R,L,F,FactoryReferenceAttribute<R,L, F>> {

}
