package de.factoryfx.javafx.view.menu;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import de.factoryfx.javafx.view.View;
import de.factoryfx.javafx.view.ViewDescription;
import de.factoryfx.javafx.view.ViewDescriptionFactory;
import de.factoryfx.javafx.view.ViewFactory;
import javafx.scene.control.MenuItem;

public class ViewMenuItemFactory<V> extends FactoryBase<MenuItem,V>{

    public final FactoryReferenceAttribute<ViewDescription,ViewDescriptionFactory<V>> viewDescription = new FactoryReferenceAttribute<>(new AttributeMetadata().de("viewDescription").en("viewDescription"),ViewDescriptionFactory.class);
    public final FactoryReferenceAttribute<View,ViewFactory<V>> view = new FactoryReferenceAttribute<>(new AttributeMetadata().de("view").en("view"),ViewFactory.class);
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("uniformDesign").en("uniformDesign"),UniformDesignFactory.class);

    @Override
    public LiveCycleController<MenuItem, V> createLifecycleController() {
        return () -> {
            MenuItem menuItem = new MenuItem();
            menuItem.setText(viewDescription.instance().text);
            uniformDesign.instance().addIcon(menuItem,viewDescription.instance().icon);

            View createdView = view.instance();

            menuItem.setOnAction(event -> {
                createdView.show();
            });
            return menuItem;
        };
    }

}