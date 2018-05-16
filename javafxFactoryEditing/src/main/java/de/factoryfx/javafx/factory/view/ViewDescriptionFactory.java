package de.factoryfx.javafx.factory.view;

import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import org.controlsfx.glyphfont.FontAwesome;

public class ViewDescriptionFactory extends SimpleFactoryBase<ViewDescription,Void,RichClientRoot> {
    public final I18nAttribute text = new I18nAttribute().de("text").en("text");
    public final EnumAttribute<FontAwesome.Glyph> icon = new EnumAttribute<>(FontAwesome.Glyph.class).de("icon").en("icon").nullable();
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");

    @Override
    public ViewDescription createImpl() {
        return new ViewDescription(text.get(),icon.getEnum(),uniformDesign.instance());
    }
}
