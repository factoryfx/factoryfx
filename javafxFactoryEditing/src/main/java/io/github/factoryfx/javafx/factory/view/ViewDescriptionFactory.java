package io.github.factoryfx.javafx.factory.view;

import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.attribute.types.I18nAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;
import org.controlsfx.glyphfont.FontAwesome;

public class ViewDescriptionFactory extends SimpleFactoryBase<ViewDescription,RichClientRoot> {
    public final I18nAttribute text = new I18nAttribute().de("text").en("text");
    public final EnumAttribute<FontAwesome.Glyph> icon = new EnumAttribute<>(FontAwesome.Glyph.class).de("icon").en("icon").nullable();
    public final FactoryReferenceAttribute<RichClientRoot,UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<RichClientRoot,UniformDesign,UniformDesignFactory>().de("uniformDesign").en("uniformDesign");

    @Override
    public ViewDescription createImpl() {
        return new ViewDescription(text.get(),icon.get(),uniformDesign.instance());
    }
}
