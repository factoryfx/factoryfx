package de.factoryfx.javafx.factory;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.factory.stage.StageFactory;
import javafx.stage.Stage;


public class RichClientRoot extends SimpleFactoryBase<Stage,Void,RichClientRoot> {
    public final FactoryReferenceAttribute<Stage, StageFactory> stageFactory = new FactoryReferenceAttribute<>(StageFactory.class);

    @Override
    public Stage createImpl() {
        return stageFactory.instance();
    }
}
