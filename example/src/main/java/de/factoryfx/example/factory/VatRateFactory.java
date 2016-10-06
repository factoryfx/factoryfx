package de.factoryfx.example.factory;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.DoubleAttribute;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.factory.LifecycleNotifier;

public class VatRateFactory extends FactoryBase<VatRate, OrderCollector>{
    public VatRateFactory(){
        setDisplayTextProvider(() -> "VatRate("+rate.get()+")");
    }

    public final DoubleAttribute rate= new DoubleAttribute(new AttributeMetadata().en("rate").addonText("%")).validation(new ObjectRequired<>());

    @Override
    protected VatRate createImp(Optional<VatRate> previousLiveObject, LifecycleNotifier<OrderCollector> lifecycle) {
        return new VatRate(rate.get());
    }
}
