package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.HashSet;

public class Immutables {

    private static final HashSet<Class<?>> immutableClasses = new HashSet<>();
    static {
        immutableClasses.add(LocalDate.class);
        immutableClasses.add(LocalDateTime.class);
        immutableClasses.add(TemporalAccessor.class);
        immutableClasses.add(String.class);
        immutableClasses.add(BigDecimal.class);
        immutableClasses.add(BigInteger.class);
    }

    public boolean contains(Class<?> clazz) {
        return immutableClasses.contains(clazz);
    }
}
