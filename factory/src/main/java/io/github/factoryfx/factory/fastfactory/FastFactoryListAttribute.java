package io.github.factoryfx.factory.fastfactory;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeCopy;
import io.github.factoryfx.factory.attribute.AttributeMatch;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FastFactoryListAttribute<R extends FactoryBase<?,R>, F extends FactoryBase<?,R>,L,V extends FactoryBase<L,R>> extends FastFactoryAttributeUtility<R,F,List<V>,FactoryListAttribute<L,V>>{

    private final Class<V> referenceClass;

    public FastFactoryListAttribute(Supplier<FactoryListAttribute<L,V>> attributeCreator, Function<F,List<V>> valueGetter, BiConsumer<F,List<V>> valueSetter, Class<V> referenceClass, String attributeName) {
        super(attributeCreator,valueGetter,valueSetter,attributeName);
        this.referenceClass = referenceClass;
    }

    @Override
    protected FactoryListAttribute<L, V> getAttribute() {
        FactoryListAttribute<L, V> attribute = super.getAttribute();
        attribute.internal_setReferenceClass(referenceClass);
        return attribute;
    }

    @Override
    public void visitChildFactory(Consumer<FactoryBase<?,?>> consumer){
        List<V> valueFactories = valueGetter.apply(boundFactory);
        for (V valueFactory : valueFactories) {
            if (valueFactory!=null){
                consumer.accept(valueFactory);
            }
        }
    }

    @Override
    public void internal_copyTo(AttributeCopy<List<V>> copyAttribute, final int level, final int maxLevel, final List<FactoryBase<?,?>> oldData, FactoryBase<?,?> parent, FactoryBase<?,?> root){
        List<V> valueFactories = valueGetter.apply(this.boundFactory);
        ArrayList<V> result = new ArrayList<>();
        for (V valueFactory : valueFactories) {
            V copy = valueFactory.internal().copyDeep(level, maxLevel, oldData, parent, root);
            result.add(copy);
        }
        copyAttribute.set(result);

    }

    @Override
    public void internal_semanticCopyTo(AttributeCopy<List<V>> copyAttribute) {
        List<V> valueFactories = valueGetter.apply(this.boundFactory);
        if (getAttribute().internal_getCopySemantic()== CopySemantic.SELF){
            copyAttribute.set(valueFactories);
        } else {
            List<V> result = new ArrayList<>();
            for (V item: valueFactories){
                final V itemCopy = item.utility().semanticCopy();
                if (itemCopy!=null){
                    result.add(itemCopy);
                }
            }
            copyAttribute.set(result);
        }
    }

    @Override
    public boolean internal_mergeMatch(AttributeMatch<List<V>> value) {
        List<V> list = valueGetter.apply(this.boundFactory);
        return internal_referenceListEquals(list, value.get());
    }

    @Override
    public void internal_merge(List<V> newList, HashMap<UUID,FactoryBase<?,?>> idToFactory){
        List<V> oldList = valueGetter.apply(boundFactory);
        if (oldList==null){
            oldList=new ArrayList<>(newList);
        }
        internal_mergeFactoryList(oldList,newList,idToFactory);
    }

}
