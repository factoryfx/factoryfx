package io.github.factoryfx.javafx.util;

import java.util.Locale;

import io.github.factoryfx.factory.attribute.primitive.BooleanAttribute;

import io.github.factoryfx.factory.attribute.types.LocaleAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.javafx.editor.attribute.ColorAttribute;
import io.github.factoryfx.javafx.RichClientRoot;
import javafx.scene.paint.Color;

public class UniformDesignFactory extends SimpleFactoryBase<UniformDesign,RichClientRoot> {

    public final LocaleAttribute locale=new LocaleAttribute().en("locale").defaultValue(Locale.ENGLISH);
    public final ColorAttribute dangerColor=new ColorAttribute().en("dangerColor").defaultValue(Color.web("#FF7979"));
    public final ColorAttribute warningColor=new ColorAttribute().en("warningColor").defaultValue(Color.web("#F0AD4E"));
    public final ColorAttribute infoColor=new ColorAttribute().en("infoColor").defaultValue(Color.web("#5BC0DE"));
    public final ColorAttribute successColor=new ColorAttribute().en("successColor").defaultValue(Color.web("#5CB85C"));
    public final ColorAttribute primaryColor=new ColorAttribute().en("primaryColor").defaultValue(Color.web("#5494CB"));
    public final ColorAttribute borderColor=new ColorAttribute().en("borderColor").defaultValue(Color.web("#B5B5B5"));
    public final BooleanAttribute askBeforeDelete = new BooleanAttribute().en("askBeforeDelete").defaultValue(false);

    @Override
    public UniformDesign createImpl() {
        return new UniformDesign(
                locale.get(),
                dangerColor.get(),
                warningColor.get(),
                infoColor.get(),
                successColor.get(),
                primaryColor.get(),
                borderColor.get(),
                askBeforeDelete.get());
    }

}
