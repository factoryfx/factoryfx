package de.factoryfx.javafx.view;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import org.controlsfx.glyphfont.FontAwesome;

public class ViewDescriptionFactory<V> extends FactoryBase<ViewDescription,V>{
    public final I18nAttribute text = new I18nAttribute(new AttributeMetadata().de("text").en("text"));
    public final ValueAttribute<FontAwesome.Glyph> icon = new ValueAttribute<>(new AttributeMetadata().de("icon").en("icon"),FontAwesome.Glyph.class);
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("uniformDesign").en("uniformDesign"),UniformDesignFactory.class);

    @Override
    public LiveCycleController<ViewDescription, V> createLifecycleController() {
        return () -> new ViewDescription(uniformDesign.instance().getText(text),icon.get());
    }
}
