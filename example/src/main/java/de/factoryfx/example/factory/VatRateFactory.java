package de.factoryfx.example.factory;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.DoubleAttribute;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveCycleController;

public class VatRateFactory extends FactoryBase<VatRate, OrderCollector>{

    public VatRateFactory(){
        setDisplayTextProvider(() -> "VatRate("+rate.get()+")");
    }
    public final DoubleAttribute rate= new DoubleAttribute(new AttributeMetadata().en("rate").addonText("%")).validation(new ObjectRequired<>());

    @Override
    public LiveCycleController<VatRate, OrderCollector> createLifecycleController() {
        return () -> new VatRate(rate.get());
    }

}
