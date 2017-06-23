package de.factoryfx.javafx.view.menu;

import de.factoryfx.data.attribute.types.EnumAttribute;
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

    public final I18nAttribute text = new I18nAttribute().de("text").en("text");
    public final EnumAttribute<Glyph> icon = new EnumAttribute<>(FontAwesome.Glyph.class).de("icon").en("icon");
    public final FactoryReferenceListAttribute<MenuItem,ViewMenuItemFactory<V>> items = new FactoryReferenceListAttribute<MenuItem,ViewMenuItemFactory<V>>().setupUnsafe(ViewMenuItemFactory.class).de("items").en("items");
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>>().setupUnsafe(UniformDesignFactory.class).de("view").en("view");

    @Override
    public Menu createImpl() {
        return createMenu();
    }

    private Menu createMenu() {
        Menu menu = new Menu();
        menu.setMnemonicParsing(true);
        menu.setText(uniformDesign.instance().getText(text));
        uniformDesign.instance().addIcon(menu,icon.getEnum());

        menu.getItems().addAll(items.instances());
        return menu;
    }

}