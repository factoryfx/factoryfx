package de.factoryfx.example.factory;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.DoubleAttribute;
import de.factoryfx.data.validation.ObjectRequired;
import de.factoryfx.factory.SimpleFactoryBase;

public class VatRateFactory extends SimpleFactoryBase<VatRate, OrderCollector> {

    public VatRateFactory(){
        config().setDisplayTextProvider(() -> "VatRate("+rate.get()+")");
    }
    public final DoubleAttribute rate= new DoubleAttribute(new AttributeMetadata().en("rate").addonText("%")).validation(new ObjectRequired<>());

    @Override
    public VatRate createImpl() {
        return new VatRate(rate.get());
    }

}
