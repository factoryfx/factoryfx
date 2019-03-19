package io.github.factoryfx.javafx.factory.view.container;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.data.util.UniformDesign;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;
import javafx.scene.control.TabPane;

public class ViewsDisplayWidgetFactory extends SimpleFactoryBase<ViewsDisplayWidget,RichClientRoot> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>(UniformDesignFactory.class).de("view").en("view");

    @Override
    public ViewsDisplayWidget createImpl() {
        return new ViewsDisplayWidget(new TabPane(),uniformDesign.instance());
    }
}