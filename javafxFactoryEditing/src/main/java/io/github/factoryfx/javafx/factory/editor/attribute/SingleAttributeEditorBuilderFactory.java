package io.github.factoryfx.javafx.factory.editor.attribute;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.javafx.factory.editor.attribute.builder.AttributeVisualisationBuilder;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;

import java.util.function.Function;

public class SingleAttributeEditorBuilderFactory extends SimpleFactoryBase<AttributeVisualisationBuilder,RichClientRoot> {
    public final FactoryReferenceAttribute<RichClientRoot, UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>();

    private final Function<UniformDesign, AttributeVisualisationBuilder> creator;
    public SingleAttributeEditorBuilderFactory(Function<UniformDesign, AttributeVisualisationBuilder> creator) {
        super();
        this.creator = creator;
    }

    SingleAttributeEditorBuilderFactory() {
        super();
        creator=null;
    }

    static {

        FactoryMetadataManager.getMetadata(SingleAttributeEditorBuilderFactory.class).setNewCopyInstanceSupplier(
                data -> new SingleAttributeEditorBuilderFactory(data.creator)
        );

    }

    @Override
    public AttributeVisualisationBuilder createImpl() {
        return creator.apply(uniformDesign.instance());
    }


}
