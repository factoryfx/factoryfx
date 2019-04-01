package io.github.factoryfx.factory;

import java.util.function.Function;

/**
 * create subtree with liveobject to instantiate only part of the tree e.g for test or in the gui
 *
 * @param <L> create Live object
 * @param <R> root
 * @param <S> sub root
 */
public class SubTreeCreator<R extends FactoryBase<?,R>, L, S extends FactoryBase<L,R>> {

    private final Function<R,S> subRootFactoryProvider;

    public SubTreeCreator(Function<R, S> subRootFactoryProvider) {
        this.subRootFactoryProvider = subRootFactoryProvider;
    }

    public L create(R root){
        final R copy = root.internal().copy();
        return subRootFactoryProvider.apply(copy).internal().create();
    }


}
