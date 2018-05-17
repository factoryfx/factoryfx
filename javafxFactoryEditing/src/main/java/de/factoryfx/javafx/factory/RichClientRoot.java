package de.factoryfx.javafx.factory;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.data.stage.BorderPaneStage;
import de.factoryfx.javafx.factory.stage.StageFactory;


public class RichClientRoot extends SimpleFactoryBase<BorderPaneStage,Void,RichClientRoot> {
    public final FactoryReferenceAttribute<BorderPaneStage, StageFactory> defaultStageFactory = new FactoryReferenceAttribute<>(StageFactory.class);

    @Override
    public BorderPaneStage createImpl() {
        return defaultStageFactory.instance();
    }
}
