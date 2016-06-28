package de.factoryfx.guimodel;

import java.util.function.Function;

public class TableColumn<T> {
    private final Function<T,String> provider;
    public final String name;

    public TableColumn(String name, Function<T, String> provider) {
        this.provider = provider;
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public String apply(Object object){
        return provider.apply((T)object);
    }
}
