package de.factoryfx.example.factory;

import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.util.DoubleAttribute;
import de.factoryfx.factory.validation.ObjectRequired;

public class VatRateFactory extends FactoryBase<VatRate,VatRateFactory>{
    {
        setDisplayTextProvider(vatRateFactory -> ""+vatRateFactory.rate.get());
    }

    public final DoubleAttribute rate= new DoubleAttribute(new AttributeMetadata().en("rate")).validation(new ObjectRequired<>());

    @Override
    protected VatRate createImp(Optional<VatRate> previousLiveObject) {
        return new VatRate(rate.get());
    }
}
