package de.factoryfx.javafx.factory.view.menu;

import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.FontAwesome.Glyph;

public class ViewMenuFactory extends SimpleFactoryBase<Menu,RichClientRoot> {

    public final I18nAttribute text = new I18nAttribute().de("text").en("text");
    public final EnumAttribute<Glyph> icon = new EnumAttribute<>(FontAwesome.Glyph.class).de("icon").en("icon").nullable();
//    public final FactoryReferenceListAttribute<MenuItem,ViewMenuItemFactory> items = new FactoryReferenceListAttribute<>(ViewMenuItemFactory.class).de("items").en("items");

    public final FactoryPolymorphicReferenceListAttribute<MenuItem> items = new FactoryPolymorphicReferenceListAttribute<>(MenuItem.class, ViewMenuItemFactory.class).de("items").en("items");

    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");

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