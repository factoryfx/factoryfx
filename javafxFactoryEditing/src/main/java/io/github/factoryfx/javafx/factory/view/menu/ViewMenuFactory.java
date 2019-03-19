package io.github.factoryfx.javafx.factory.view.menu;

import io.github.factoryfx.data.attribute.types.EnumAttribute;
import io.github.factoryfx.data.attribute.types.I18nAttribute;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryPolymorphicReferenceListAttribute;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;
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