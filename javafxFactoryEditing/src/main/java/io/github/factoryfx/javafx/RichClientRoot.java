package io.github.factoryfx.javafx;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.stage.StageFactory;
import javafx.stage.Stage;


public class RichClientRoot extends SimpleFactoryBase<Stage,RichClientRoot> {
    public final FactoryAttribute<RichClientRoot,Stage, StageFactory> stageFactory = new FactoryAttribute<>();

    @Override
    protected Stage createImpl() {
        return stageFactory.instance();
    }
}
