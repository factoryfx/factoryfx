package io.github.factoryfx.javafx.factory.editor.attribute;

import io.github.factoryfx.data.DataDictionary;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.editor.attribute.builder.AttributeVisualisationBuilder;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;

import java.util.function.Function;

public class SingleAttributeEditorBuilderFactory extends SimpleFactoryBase<AttributeVisualisationBuilder,RichClientRoot> {
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");

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

        DataDictionary.getDataDictionary(SingleAttributeEditorBuilderFactory.class).setNewCopyInstanceSupplier(
                data -> new SingleAttributeEditorBuilderFactory(data.creator)
        );

    }

    @Override
    public AttributeVisualisationBuilder createImpl() {
        return creator.apply(uniformDesign.instance());
    }


}
