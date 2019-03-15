package de.factoryfx.javafx.factory.view.menu;

import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.javafx.factory.RichClientRoot;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class SeparatorMenuItemFactory extends PolymorphicFactoryBase<MenuItem,RichClientRoot> {

    @Override
    public MenuItem createImpl() {
        return new SeparatorMenuItem();
    }

}