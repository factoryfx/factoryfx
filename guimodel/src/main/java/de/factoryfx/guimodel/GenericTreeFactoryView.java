package de.factoryfx.guimodel;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;

public class GenericTreeFactoryView<T extends FactoryBase<? extends LiveObject, ? extends FactoryBase>> {

    private final T root;

    public GenericTreeFactoryView(T root) {
        this.root = root;
    }

    public T getRoot(){
        return root;
    }
}
