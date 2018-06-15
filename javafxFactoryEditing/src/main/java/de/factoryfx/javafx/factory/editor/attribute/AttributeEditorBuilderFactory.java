package de.factoryfx.javafx.factory.editor.attribute;

import java.util.function.Function;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.data.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;

public class AttributeEditorBuilderFactory extends SimpleFactoryBase<AttributeEditorBuilder,Void,RichClientRoot> {
    public final FactoryReferenceListAttribute<SingleAttributeEditorBuilder<?>,SingleAttributeEditorBuilderFactory> editors = new FactoryReferenceListAttribute<>();

    @Override
    public AttributeEditorBuilder createImpl() {
        return new AttributeEditorBuilder(editors.instances());
    }

    public AttributeEditorBuilderFactory(){
        super();
    }


}
