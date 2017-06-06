package de.factoryfx.javafx.view;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import org.controlsfx.glyphfont.FontAwesome;

public class ViewDescriptionFactory<V> extends SimpleFactoryBase<ViewDescription,V> {
    public final I18nAttribute text = new I18nAttribute(new AttributeMetadata().de("text").en("text"));
    EnumAttribute<FontAwesome.Glyph> icon = new EnumAttribute<>(FontAwesome.Glyph.class,new AttributeMetadata().de("icon").en("icon"));
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("uniformDesign").en("uniformDesign"),UniformDesignFactory.class);

    @Override
    public ViewDescription createImpl() {
        return new ViewDescription(uniformDesign.instance().getText(text),icon.get());
    }
}
