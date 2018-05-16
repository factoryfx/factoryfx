package de.factoryfx.example.server.shop;

import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.example.server.ServerBaseFactory;

public class VatRateFactory extends ServerBaseFactory<VatRate> {

    public final DoubleAttribute rate= new DoubleAttribute().en("rate").addonText("%");

    public VatRateFactory(){
        config().setDisplayTextProvider(() -> "VatRate("+rate.get()+")");
    }

    @Override
    public VatRate createImpl() {
        return new VatRate(rate.get());
    }

}
