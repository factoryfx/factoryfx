package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.attribute.ImmutableValueAttribute;

import java.math.BigInteger;

public class BigIntegerAttribute extends ImmutableValueAttribute<BigInteger, BigIntegerAttribute> {

    public BigIntegerAttribute() {
        super(BigInteger.class);
    }

}