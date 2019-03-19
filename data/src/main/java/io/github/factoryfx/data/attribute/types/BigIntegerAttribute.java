package io.github.factoryfx.data.attribute.types;

import io.github.factoryfx.data.attribute.ImmutableValueAttribute;

import java.math.BigInteger;

public class BigIntegerAttribute extends ImmutableValueAttribute<BigInteger, BigIntegerAttribute> {

    public BigIntegerAttribute() {
        super(BigInteger.class);
    }

}