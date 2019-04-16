package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.FactoryEnclosingAttributeVisitor;
import io.github.factoryfx.factory.attribute.Attribute;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface FactoryChildrenEnclosingAttribute<R extends FactoryBase<?,R>,A> {
    /**
     *
     * @param root factory tree root
     * @param parent data that contains the attribute
     */
    void internal_addBackReferences(R root, FactoryBase<?,R> parent);

    /**setup value selection and new value adding for user editing
     * @param clazz class
     * */
    @SuppressWarnings("unchecked")
    public void internal_setReferenceClass(Class<?> clazz);

    @SuppressWarnings("unchecked")
    default void internal_copyToUnsafe(Attribute<?,?> copyAttribute, final int level, final int maxLevel, final List<FactoryBase<?,R>> oldData, FactoryBase<?,R> parent, R root){
        internal_copyTo((A)copyAttribute, level,maxLevel, oldData, parent, root);
    }

    void internal_copyTo(A copyAttribute, final int level, final int maxLevel, final List<FactoryBase<?,R>> oldData, FactoryBase<?,R> parent, R root);

    void internal_visitChildren(Consumer<FactoryBase<?,R>> consumer, boolean includeViews);
    /*
    see test {{@Link MergeTest#test_duplicate_ids_bug}} why this is needed
*/
    <RL extends FactoryBase<?,RL>> void internal_fixDuplicateObjects(Map<UUID, FactoryBase<?,RL>> idToDataMap);

}
