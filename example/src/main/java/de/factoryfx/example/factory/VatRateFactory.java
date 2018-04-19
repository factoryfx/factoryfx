package de.factoryfx.example.factory;

import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.factory.SimpleFactoryBase;

public class VatRateFactory extends SimpleFactoryBase<VatRate, OrderCollector,ShopFactory> {

    public final DoubleAttribute rate= new DoubleAttribute().en("rate").addonText("%");

    public VatRateFactory(){
        config().setDisplayTextProvider(() -> "VatRate("+rate.get()+")");
    }

    @Override
    public VatRate createImpl() {
        return new VatRate(rate.get());
    }

}
