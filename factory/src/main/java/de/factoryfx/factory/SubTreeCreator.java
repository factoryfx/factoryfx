package de.factoryfx.factory;

import java.util.function.Function;

import de.factoryfx.data.Data;

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

    @SuppressWarnings("unchecked")
    public L create(Data data){
        //TODO add generic root to data?
        R rootFactory= (R) data.internal().getRoot();
        final R copy = rootFactory.internal().copyRoot();
        return subRootFactoryProvider.apply(copy).internalFactory().create();
    }


}
