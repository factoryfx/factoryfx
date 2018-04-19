package de.factoryfx.javafx.editor;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;

public class DataEditorFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<DataEditor,V,R> {

    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V,R>> uniformDesign = new FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V,R>>().setupUnsafe(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<AttributeEditorBuilder,AttributeEditorBuilderFactory<V,R>> attributeEditorBuilder = new FactoryReferenceAttribute<AttributeEditorBuilder,AttributeEditorBuilderFactory<V,R>>().de("uniformDesign").en("uniformDesign");

    @Override
    public DataEditor createImpl() {
        return new DataEditor(attributeEditorBuilder.instance(),uniformDesign.instance());
    }
}