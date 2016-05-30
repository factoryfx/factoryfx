package de.factoryfx.guimodel;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by henning on 30.05.2016.
 */
public class ViewEditorModel<T extends FactoryEditorModel> {
    private final Supplier<List<T>> listProvider;
    private final Optional<Supplier<T>> adder;
    private final Consumer<T> remover;

    public ViewEditorModel(Supplier<List<T>> listProvider, Optional<Supplier<T>> adder, Consumer<T> remover) {
        this.listProvider = listProvider;
        this.adder = adder;
        this.remover = remover;
    }
}
