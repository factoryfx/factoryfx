package io.github.factoryfx.javafx.editor.attribute;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.javafx.editor.attribute.builder.AttributeVisualisationBuilder;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.util.UniformDesignFactory;

import java.util.function.Function;

public class SingleAttributeEditorBuilderFactory extends SimpleFactoryBase<AttributeVisualisationBuilder,RichClientRoot> {
    public final FactoryAttribute<RichClientRoot, UniformDesign, UniformDesignFactory> uniformDesign = new FactoryAttribute<>();

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
    protected AttributeVisualisationBuilder createImpl() {
        return creator.apply(uniformDesign.instance());
    }


}
