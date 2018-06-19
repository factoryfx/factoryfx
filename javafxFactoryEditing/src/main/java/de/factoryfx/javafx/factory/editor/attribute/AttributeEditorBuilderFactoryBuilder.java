package de.factoryfx.javafx.factory.editor.attribute;

import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;

import java.util.function.Function;

public class AttributeEditorBuilderFactoryBuilder {

    @SuppressWarnings("unchecked")
    public AttributeEditorBuilderFactory build(UniformDesignFactory uniformDesignFactory){
        AttributeEditorBuilderFactory factory = new AttributeEditorBuilderFactory();
        for (Function<UniformDesign, SingleAttributeEditorBuilder<?>> creator : AttributeEditorBuilder.createDefaultSingleAttributeEditorBuildersFunctions()) {
            SingleAttributeEditorBuilderFactory singleAttributeEditorBuilderFactory = new SingleAttributeEditorBuilderFactory(creator);
            singleAttributeEditorBuilderFactory.uniformDesign.set(uniformDesignFactory);
            factory.editors.add(singleAttributeEditorBuilderFactory);
        }
        return factory;
    }

}
