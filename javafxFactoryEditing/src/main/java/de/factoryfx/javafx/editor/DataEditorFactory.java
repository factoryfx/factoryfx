package de.factoryfx.javafx.editor;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;

public class DataEditorFactory extends SimpleFactoryBase<DataEditor,Void> {

    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<Void>> uniformDesign = new FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<Void>>().setupUnsafe(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");
    public final FactoryReferenceAttribute<AttributeEditorBuilder,AttributeEditorBuilderFactory<Void>> attributeEditorBuilder = new FactoryReferenceAttribute<AttributeEditorBuilder,AttributeEditorBuilderFactory<Void>>().de("uniformDesign").en("uniformDesign");

    @Override
    public DataEditor createImpl() {
        return new DataEditor(attributeEditorBuilder.instance(),uniformDesign.instance());
    }
}