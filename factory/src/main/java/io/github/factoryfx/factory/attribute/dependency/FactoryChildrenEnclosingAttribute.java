package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryBase;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface FactoryChildrenEnclosingAttribute {

    void internal_visitChildren(Consumer<FactoryBase<?,?>> consumer, boolean includeViews);
    /*
    see test {@Link MergeTest#test_duplicate_ids_bug} why this is needed
    */
    <RL extends FactoryBase<?,RL>> void internal_fixDuplicateObjects(Map<UUID, FactoryBase<?,RL>> idToDataMap);

    /*
     * @param root factory tree root
     * @param parent data that contains the attribute
     */
    void internal_addBackReferences(FactoryBase<?,?> root, FactoryBase<?,?> parent);

}
