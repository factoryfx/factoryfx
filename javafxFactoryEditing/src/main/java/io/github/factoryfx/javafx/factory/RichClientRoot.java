package io.github.factoryfx.javafx.factory;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.factory.stage.StageFactory;
import javafx.stage.Stage;


public class RichClientRoot extends SimpleFactoryBase<Stage,RichClientRoot> {
    public final FactoryReferenceAttribute<Stage, StageFactory> stageFactory = new FactoryReferenceAttribute<>(StageFactory.class);

    @Override
    public Stage createImpl() {
        return stageFactory.instance();
    }
}
