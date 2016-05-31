package de.factoryfx.guimodel;

import java.util.List;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;

public class ViewManager<T extends FactoryBase<? extends LiveObject, T>> {
    private final T rootFactory;
    private final List<View> views;

    public ViewManager(T rootFactory, List<View> views) {
        this.rootFactory = rootFactory;
        this.views = views;
    }

    public List<View> getViews(){
        return views;
    }

}
