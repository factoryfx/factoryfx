package io.github.factoryfx.javafx.view.container;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.util.UniformDesignFactory;
import javafx.scene.control.TabPane;

public class ViewsDisplayWidgetFactory extends SimpleFactoryBase<ViewsDisplayWidget,RichClientRoot> {
    public final FactoryAttribute<UniformDesign,UniformDesignFactory> uniformDesign = new FactoryAttribute<>();

    @Override
    protected ViewsDisplayWidget createImpl() {
        return new ViewsDisplayWidget(new TabPane(),uniformDesign.instance());
    }
}