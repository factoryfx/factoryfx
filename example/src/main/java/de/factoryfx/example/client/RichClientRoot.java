package de.factoryfx.example.client;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.stage.BorderPaneStage;
import de.factoryfx.javafx.stage.DefaultStageFactory;

public class RichClientRoot extends SimpleFactoryBase<BorderPaneStage,Void,RichClientRoot> {
    public final FactoryReferenceAttribute<BorderPaneStage, DefaultStageFactory<RichClientRoot>> defaultStageFactory = new FactoryReferenceAttribute<BorderPaneStage, DefaultStageFactory<RichClientRoot>>().setupUnsafe(DefaultStageFactory.class);


    @Override
    public BorderPaneStage createImpl() {
        return defaultStageFactory.instance();
    }
}
