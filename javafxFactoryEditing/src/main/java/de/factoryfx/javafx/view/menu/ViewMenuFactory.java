package de.factoryfx.javafx.view.menu;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.ValueAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;

public class ViewMenuFactory<V> extends SimpleFactoryBase<Menu,V> {

    public final I18nAttribute text = new I18nAttribute(new AttributeMetadata().de("text").en("text"));
    public final ValueAttribute<Glyph> icon = new ValueAttribute<>(new AttributeMetadata().de("icon").en("icon"),FontAwesome.Glyph.class);
    public final FactoryReferenceListAttribute<MenuItem,ViewMenuItemFactory<V>> items = new FactoryReferenceListAttribute<>(new AttributeMetadata().de("items").en("items"),ViewMenuItemFactory.class);
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("view").en("view"),UniformDesignFactory.class);

    @Override
    public Menu createImpl() {
        return createMenu();
    }

    private Menu createMenu() {
        Menu menu = new Menu();
        menu.setMnemonicParsing(true);
        menu.setText(uniformDesign.instance().getText(text));
        uniformDesign.instance().addIcon(menu,icon.get());

        menu.getItems().addAll(items.instances());
        return menu;
    }

}