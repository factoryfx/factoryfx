package de.factoryfx.guimodel;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;

public class GenericEditor<T extends FactoryBase<? extends LiveObject, ? extends FactoryBase>> {

    private final T root;

    public GenericEditor(T root) {
        this.root = root;
    }

    public T getRoot(){
        return root;
    }
}
