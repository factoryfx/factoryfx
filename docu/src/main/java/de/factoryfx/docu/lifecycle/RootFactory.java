package de.factoryfx.docu.lifecycle;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RootFactory extends FactoryBase<Root,Void> {

    public RootFactory(){
        configLiveCycle().setCreator(() -> new Root());
        configLiveCycle().setStarter(root -> root.start());
        configLiveCycle().setDestroyer(root -> root.destroy());
    }
}
