package de.factoryfx.javafx.factory.editor;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.factory.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.data.editor.data.DataEditor;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DataEditorFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<DataEditor,V,R> {

    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V,R>> uniformDesign = new FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V,R>>().setupUnsafe(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<AttributeEditorBuilder,AttributeEditorBuilderFactory<V,R>> attributeEditorBuilder = new FactoryReferenceAttribute<AttributeEditorBuilder,AttributeEditorBuilderFactory<V,R>>().de("uniformDesign").en("uniformDesign");

    @Override
    public DataEditor createImpl() {
        return new DataEditor(attributeEditorBuilder.instance(),uniformDesign.instance());
    }
}