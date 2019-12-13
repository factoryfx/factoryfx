package io.github.factoryfx.javafx.view.menu;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.javafx.RichClientRoot;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class SeparatorMenuItemFactory extends SimpleFactoryBase<MenuItem,RichClientRoot> {

    @Override
    protected MenuItem createImpl() {
        return new SeparatorMenuItem();
    }

}