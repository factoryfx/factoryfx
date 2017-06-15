package de.factoryfx.javafx.view.menu;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import de.factoryfx.javafx.view.View;
import de.factoryfx.javafx.view.ViewDescription;
import de.factoryfx.javafx.view.ViewDescriptionFactory;
import de.factoryfx.javafx.view.ViewFactory;
import javafx.scene.control.MenuItem;

public class ViewMenuItemFactory<V> extends SimpleFactoryBase<MenuItem,V> {

    public final FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V>> viewDescription = new FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V>>().setupUnsafe(ViewDescriptionFactory.class).de("viewDescription").en("viewDescription");
    public final FactoryReferenceAttribute<View,ViewFactory<V>> view = new FactoryReferenceAttribute<View,ViewFactory<V>>().setupUnsafe(ViewFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>>().setupUnsafe(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");

    @Override
    public MenuItem createImpl() {
        MenuItem menuItem = new MenuItem();
        menuItem.setText(viewDescription.instance().text);
        uniformDesign.instance().addIcon(menuItem,viewDescription.instance().icon);

        View createdView = view.instance();

        menuItem.setOnAction(event -> {
            createdView.show();
        });
        return menuItem;
    }

}