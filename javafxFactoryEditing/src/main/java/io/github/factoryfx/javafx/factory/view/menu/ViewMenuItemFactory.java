package io.github.factoryfx.javafx.factory.view.menu;

import io.github.factoryfx.factory.PolymorphicFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.view.View;
import io.github.factoryfx.javafx.factory.view.ViewDescription;
import io.github.factoryfx.javafx.factory.view.ViewDescriptionFactory;
import io.github.factoryfx.javafx.factory.view.ViewFactory;
import javafx.scene.control.MenuItem;

public class ViewMenuItemFactory extends PolymorphicFactoryBase<MenuItem,RichClientRoot> {

    public final FactoryAttribute<RichClientRoot,ViewDescription,ViewDescriptionFactory> viewDescription = new FactoryAttribute<>();
    public final FactoryAttribute<RichClientRoot,View,ViewFactory> view = new FactoryAttribute<>();

    @Override
    public MenuItem createImpl() {
        MenuItem menuItem = new MenuItem();
        ViewDescription viewDescription = this.viewDescription.instance();
        viewDescription.describeMenuItem(menuItem);

        View createdView = view.instance();

        menuItem.setOnAction(event -> {
            createdView.show();
        });
        return menuItem;
    }

}