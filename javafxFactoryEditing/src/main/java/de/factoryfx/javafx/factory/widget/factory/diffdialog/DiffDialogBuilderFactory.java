package de.factoryfx.javafx.factory.widget.factory.diffdialog;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DiffDialogBuilderFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<DiffDialogBuilder,V,R> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V,R>> uniformDesign = new FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V,R>>().setupUnsafe(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<AttributeEditorBuilder,AttributeEditorBuilderFactory<V,R>> attributeEditorBuilder = new FactoryReferenceAttribute<AttributeEditorBuilder,AttributeEditorBuilderFactory<V,R>>().setupUnsafe(AttributeEditorBuilderFactory.class).de("uniformDesign").en("uniformDesign");


    @Override
    public DiffDialogBuilder createImpl() {
        return new DiffDialogBuilder(uniformDesign.instance(),attributeEditorBuilder.instance());
    }
}