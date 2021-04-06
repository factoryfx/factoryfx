package io.github.factoryfx.factory.record;

import io.github.factoryfx.factory.AttributeMetadataVisitor;
import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.AttributeAndMetadata;
import io.github.factoryfx.factory.fastfactory.FastFactoryUtilityInterface;
import io.github.factoryfx.factory.record.Dependency;
import io.github.factoryfx.factory.record.RecordFactory;

import java.util.List;
import java.util.function.Consumer;

public class RecordFactoryUtility<R extends FactoryBase<?, R>, F extends FactoryBase<?, R>> implements FastFactoryUtilityInterface<R,F> {
    @Override
    public void visitAttributesFlat(F factory, AttributeVisitor attributeVisitor) {
        for (AttributeAndMetadata attributeAndMetadata : ((RecordFactory<?,?,?>) factory).dep().getAttributes()) {
            attributeVisitor.accept(attributeAndMetadata.attributeMetadata,attributeAndMetadata.attribute);
        }
    }

    @Override
    public void visitAttributesForCopy(F factory, F other, FactoryBase.BiCopyAttributeVisitor<?> consumer) {
        List<AttributeAndMetadata> attributesFactory = ((RecordFactory<?, ?,  ?>) factory).dep().getAttributes();
        List<AttributeAndMetadata> attributesOther = ((RecordFactory<?, ?, ?>) other).dep().getAttributes();
        for (int i = 0; i < attributesFactory.size(); i++) {
            AttributeAndMetadata attributeAndMetadataFactory = attributesFactory.get(i);
            AttributeAndMetadata attributeAndMetadataOther = attributesOther.get(i);
            ((FactoryBase.BiCopyAttributeVisitor)consumer).accept(attributeAndMetadataFactory.attribute, attributeAndMetadataOther.attribute);
        }
    }

    @Override
    public <V> void visitAttributesForMatch(F factory, F other, FactoryBase.AttributeMatchVisitor<V> consumer) {
        List<AttributeAndMetadata> attributesFactory = ((RecordFactory<?, ?, ?>) factory).dep().getAttributes();
        List<AttributeAndMetadata> attributesOther = ((RecordFactory<?, ?, ?>) other).dep().getAttributes();
        for (int i = 0; i < attributesFactory.size(); i++) {
            AttributeAndMetadata attributeAndMetadataFactory = attributesFactory.get(i);
            AttributeAndMetadata attributeAndMetadataOther = attributesOther.get(i);
            ((FactoryBase.AttributeMatchVisitor)consumer).accept(attributeAndMetadataFactory.attributeMetadata.attributeVariableName,attributeAndMetadataFactory.attribute, attributeAndMetadataOther.attribute);
        }
    }

    @Override
    public void visitChildFactoriesAndViewsFlat(F factory, Consumer<FactoryBase<?, ?>> consumer) {
        for (AttributeAndMetadata attributeAndMetadata : ((RecordFactory<?,?,?>) factory).dep().getAttributes()) {
            if (attributeAndMetadata.attribute instanceof Dependency<?,?>) {
                FactoryBase<?,?> child = (FactoryBase<?, ?>) attributeAndMetadata.attribute.get();
                if (child!=null){
                    consumer.accept(child);
                }
            }
        }
    }

    @Override
    public void visitAttributesTripleFlat(F factory, F other1, F other2, FactoryBase.TriAttributeVisitor<?> consumer) {
        List<AttributeAndMetadata> attributeList1=((RecordFactory<?, ?,  ?>) factory).dep().getAttributes();
        List<AttributeAndMetadata> attributeList2=((RecordFactory<?, ?,  ?>) other1).dep().getAttributes();
        List<AttributeAndMetadata> attributeList3=((RecordFactory<?, ?,  ?>) other2).dep().getAttributes();

        for (int i = 0; i < attributeList1.size(); i++) {
            AttributeAndMetadata attribute1 = attributeList1.get(i);
            AttributeAndMetadata attribute2 = attributeList2.get(i);
            AttributeAndMetadata attribute3 = attributeList3.get(i);
            visitAttributeTripleFlat(attribute1,attribute2,attribute3,consumer);
        }
    }

    @SuppressWarnings("unchecked")
    private <V> void visitAttributeTripleFlat(AttributeAndMetadata factory, AttributeAndMetadata other1, AttributeAndMetadata other2, FactoryBase.TriAttributeVisitor consumer) {
        consumer.accept(factory.attributeMetadata.attributeVariableName, factory.attribute,other1.attribute,other2.attribute);
    }

    @Override
    public void visitAttributesMetadataFlat(AttributeMetadataVisitor consumer) {

    }
}
