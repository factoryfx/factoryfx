package de.factoryfx.javafx.factory.view.menu;

import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.view.View;
import de.factoryfx.javafx.factory.view.ViewDescription;
import de.factoryfx.javafx.factory.view.ViewDescriptionFactory;
import de.factoryfx.javafx.factory.view.ViewFactory;
import javafx.scene.control.MenuItem;

public class ViewMenuItemFactory extends PolymorphicFactoryBase<MenuItem,RichClientRoot> {

    public final FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory> viewDescription = new FactoryReferenceAttribute<>(ViewDescriptionFactory.class).de("viewDescription").en("viewDescription");
    public final FactoryReferenceAttribute<View,ViewFactory> view = new FactoryReferenceAttribute<>(ViewFactory.class).de("view").en("view");

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