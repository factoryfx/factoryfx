package de.factoryfx.data.attribute.types;

import de.factoryfx.data.attribute.ImmutableValueAttribute;
import java.math.BigInteger;

public class BigIntegerAttribute extends ImmutableValueAttribute<BigInteger, BigIntegerAttribute> {

    public BigIntegerAttribute() {
        super(BigInteger.class);
    }

}