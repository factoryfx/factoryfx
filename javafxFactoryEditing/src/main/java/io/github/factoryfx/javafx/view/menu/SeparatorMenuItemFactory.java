package io.github.factoryfx.javafx.view.menu;

import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.javafx.RichClientRoot;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class SeparatorMenuItemFactory extends PolymorphicFactoryBase<MenuItem,RichClientRoot> {

    @Override
    public MenuItem createImpl() {
        return new SeparatorMenuItem();
    }

}