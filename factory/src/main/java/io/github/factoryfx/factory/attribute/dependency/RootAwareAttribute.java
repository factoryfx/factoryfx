package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;

import java.util.List;

public interface RootAwareAttribute<R extends FactoryBase<?,R>,A> {
    /**
     *
     * @param root factory tree root
     * @param parent data that contains the attribute
     */
    void internal_addBackReferences(R root, FactoryBase<?,R> parent);

    @SuppressWarnings("unchecked")
    default void internal_copyToUnsafe(Attribute<?,?> copyAttribute, final int level, final int maxLevel, final List<FactoryBase<?,R>> oldData, FactoryBase<?,R> parent, R root){
        internal_copyTo((A)copyAttribute, level,maxLevel, oldData, parent, root);
    }

    void internal_copyTo(A copyAttribute, final int level, final int maxLevel, final List<FactoryBase<?,R>> oldData, FactoryBase<?,R> parent, R root);
}
