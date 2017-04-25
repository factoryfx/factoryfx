package de.factoryfx.javafx.editor.attribute;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import de.factoryfx.javafx.widget.diffdialog.DiffDialogBuilder;

public class AttributeEditorBuilderFactory<V> extends SimpleFactoryBase<AttributeEditorBuilder,V> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("uniformDesign").en("uniformDesign"),UniformDesignFactory.class);

    @Override
    public AttributeEditorBuilder createImpl() {
        return new AttributeEditorBuilder(uniformDesign.instance());
    }
}
