package de.factoryfx.factory.builder;

import java.util.Objects;

public class FactoryCreatorIdentifier {
    private final Class<?> clazz;
    private final String name;

    public FactoryCreatorIdentifier(Class<?> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactoryCreatorIdentifier that = (FactoryCreatorIdentifier) o;
        return Objects.equals(clazz, that.clazz) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, name);
    }
}
