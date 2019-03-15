package de.factoryfx.javafx.factory.view.container;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.util.UniformDesign;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.javafx.factory.util.UniformDesignFactory;
import javafx.scene.control.TabPane;

public class ViewsDisplayWidgetFactory extends SimpleFactoryBase<ViewsDisplayWidget,RichClientRoot> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("view").en("view");

    @Override
    public ViewsDisplayWidget createImpl() {
        return new ViewsDisplayWidget(new TabPane(),uniformDesign.instance());
    }
}