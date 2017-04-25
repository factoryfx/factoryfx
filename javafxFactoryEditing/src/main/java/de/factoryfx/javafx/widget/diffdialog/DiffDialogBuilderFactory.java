package de.factoryfx.javafx.widget.diffdialog;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilderFactory;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;

public class DiffDialogBuilderFactory<V> extends SimpleFactoryBase<DiffDialogBuilder,V> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("uniformDesign").en("uniformDesign"),UniformDesignFactory.class);
    public final FactoryReferenceAttribute<AttributeEditorBuilder,AttributeEditorBuilderFactory<V>> attributeEditorBuilder = new FactoryReferenceAttribute<>(new AttributeMetadata().de("uniformDesign").en("uniformDesign"),AttributeEditorBuilderFactory.class);


    @Override
    public DiffDialogBuilder createImpl() {
        return new DiffDialogBuilder(uniformDesign.instance(),attributeEditorBuilder.instance());
    }
}