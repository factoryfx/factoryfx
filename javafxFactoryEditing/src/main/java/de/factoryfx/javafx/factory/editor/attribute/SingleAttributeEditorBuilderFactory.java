package de.factoryfx.javafx.factory.editor.attribute;

import de.factoryfx.data.DataDictionary;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;

import java.util.function.Function;

public class SingleAttributeEditorBuilderFactory extends SimpleFactoryBase<SingleAttributeEditorBuilder<?>,Void,RichClientRoot> {
    public final FactoryReferenceAttribute<UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");

    private final Function<UniformDesign,SingleAttributeEditorBuilder<?>> creator;
    public SingleAttributeEditorBuilderFactory(Function<UniformDesign, SingleAttributeEditorBuilder<?>> creator) {
        super();
        this.creator = creator;
    }

//    public SingleAttributeEditorBuilderFactory() {
//        super();
//        creator=null;
//    }

    static {

        DataDictionary.getDataDictionary(SingleAttributeEditorBuilderFactory.class).setNewCopyInstanceSupplier(
                data -> new SingleAttributeEditorBuilderFactory(data.creator)
        );

    }

    @Override
    public SingleAttributeEditorBuilder<?> createImpl() {
        return creator.apply(uniformDesign.instance());
    }


}