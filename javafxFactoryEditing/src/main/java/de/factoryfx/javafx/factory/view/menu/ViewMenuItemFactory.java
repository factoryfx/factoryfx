package de.factoryfx.javafx.factory.view.menu;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import de.factoryfx.javafx.factory.view.View;
import de.factoryfx.javafx.factory.view.ViewDescription;
import de.factoryfx.javafx.factory.view.ViewDescriptionFactory;
import de.factoryfx.javafx.factory.view.ViewFactory;
import javafx.scene.control.MenuItem;

public class ViewMenuItemFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<MenuItem,V,R> {

    public final FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V,R>> viewDescription = new FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V,R>>().setupUnsafe(ViewDescriptionFactory.class).de("viewDescription").en("viewDescription");
    public final FactoryReferenceAttribute<View,ViewFactory<V,R>> view = new FactoryReferenceAttribute<View,ViewFactory<V,R>>().setupUnsafe(ViewFactory.class).de("view").en("view");
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V,R>> uniformDesign = new FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V,R>>().setupUnsafe(UniformDesignFactory.class).de("uniformDesign").en("uniformDesign");

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