package io.github.factoryfx.factory;

import java.util.function.Function;

/**
 * create subtree with liveobject to instantiate only part of the tree e.g for test or in the gui
 *
 * @param <L> create Live object
 * @param <R> root
 * @param <S> sub root
 */
public class SubTreeCreator<L, R extends FactoryBase<?,R>, S extends FactoryBase<L,R>> {

    private final Function<R,S> subRootFactoryProvider;

    public SubTreeCreator(Function<R, S> subRootFactoryProvider) {
        this.subRootFactoryProvider = subRootFactoryProvider;
    }

    @SuppressWarnings("unchecked")
    public L create(FactoryBase<?,?> data){
        //TODO add generic root to data?
        R rootFactory= (R) data.internal().getRoot();
        final R copy = rootFactory.internal().copy();
        return subRootFactoryProvider.apply(copy).internal().create();
    }


}
