package io.github.factoryfx.javafx.factory.view.container;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;
import javafx.scene.control.TabPane;

public class ViewsDisplayWidgetFactory extends SimpleFactoryBase<ViewsDisplayWidget,RichClientRoot> {
    public final FactoryAttribute<RichClientRoot,UniformDesign,UniformDesignFactory> uniformDesign = new FactoryAttribute<>();

    @Override
    public ViewsDisplayWidget createImpl() {
        return new ViewsDisplayWidget(new TabPane(),uniformDesign.instance());
    }
}