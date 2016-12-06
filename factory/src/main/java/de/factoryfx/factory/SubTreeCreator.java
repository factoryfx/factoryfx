package de.factoryfx.factory;

import java.util.function.Function;

/**
 * create subtree with liveobject to instantiate only part of the tree e.g for test or in the gui
 *
 * @param <L> create Live object
 * @param <V> visitor
 * @param <R> root
 * @param <S> subroot
 */
public class SubTreeCreator<L,V, R extends FactoryBase<?,V>, S extends FactoryBase<L,V>> {

    private final Function<R,S> subRootFactoryProvider;

    public SubTreeCreator(Function<R, S> subRootFactoryProvider) {
        this.subRootFactoryProvider = subRootFactoryProvider;
    }

    public L create(R rootFactory){
        final R copy = rootFactory.internal().copy().internal().prepareUsage();
        return subRootFactoryProvider.apply(copy).createLifecycleController().create();
    }


}
