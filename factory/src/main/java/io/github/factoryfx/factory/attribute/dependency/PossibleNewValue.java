package io.github.factoryfx.factory.attribute.dependency;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;

import java.util.function.Consumer;

public class PossibleNewValue<F extends FactoryBase<?,?>> {

    private final Consumer<F> adder;
    public final F newValue;
    private final FactoryBase<?,?> root;

    public PossibleNewValue(Consumer<F> adder, F newValue, FactoryBase<?, ?> root) {
        this.adder = adder;
        this.newValue = newValue;
        this.root = root;
    }

    public void add(){
        adder.accept(newValue);
        root.internal().finalise();
    }

    @SuppressWarnings("unchecked")
    public void addSemanticCopy(){
        adder.accept((F)newValue.utility().semanticCopy());
        root.internal().finalise();
    }
}
