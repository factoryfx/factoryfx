package de.factoryfx.javafx.util;

import java.util.Locale;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.ColorAttribute;
import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import javafx.scene.paint.Color;

public class UniformDesignFactory<V> extends FactoryBase<UniformDesign,V>{

    public final LocaleAttribute locale=new LocaleAttribute(new AttributeMetadata().en("locale")).defaultValue(Locale.ENGLISH);
    public final ColorAttribute dangerColor=new ColorAttribute(new AttributeMetadata().en("dangerColor")).defaultValue(Color.web("#FF7979"));
    public final ColorAttribute warningColor=new ColorAttribute(new AttributeMetadata().en("warningColor")).defaultValue(Color.web("#F0AD4E"));
    public final ColorAttribute infoColor=new ColorAttribute(new AttributeMetadata().en("infoColor")).defaultValue(Color.web("#5BC0DE"));
    public final ColorAttribute successColor=new ColorAttribute(new AttributeMetadata().en("successColor")).defaultValue(Color.web("#5CB85C"));
    public final ColorAttribute primaryColor=new ColorAttribute(new AttributeMetadata().en("primaryColor")).defaultValue(Color.web("#5494CB"));
    public final ColorAttribute borderColor=new ColorAttribute(new AttributeMetadata().en("borderColor")).defaultValue(Color.web("#B5B5B5"));

    @Override
    public LiveCycleController<UniformDesign, V> createLifecycleController() {
        return () -> new UniformDesign(
                    locale.get(),
                    dangerColor.get(),
                    warningColor.get(),
                    infoColor.get(),
                    successColor.get(),
                    primaryColor.get(),
                    borderColor.get()
        );
    }
}
