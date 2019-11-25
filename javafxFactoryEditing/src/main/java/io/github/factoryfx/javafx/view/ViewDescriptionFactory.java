package io.github.factoryfx.javafx.view;

import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.attribute.types.I18nAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.util.UniformDesignFactory;
import org.controlsfx.glyphfont.FontAwesome;

public class ViewDescriptionFactory extends SimpleFactoryBase<ViewDescription,RichClientRoot> {
    public final I18nAttribute text = new I18nAttribute().de("text").en("text");
    public final EnumAttribute<FontAwesome.Glyph> icon = new EnumAttribute<FontAwesome.Glyph>().de("icon").en("icon").nullable();
    public final FactoryAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryAttribute<UniformDesign,UniformDesignFactory>().de("uniformDesign").en("uniformDesign");

    @Override
    protected ViewDescription createImpl() {
        return new ViewDescription(text.get(),icon.get(),uniformDesign.instance());
    }
}
