package de.factoryfx.richclient.framework.view;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.richclient.framework.widget.Widget;
import javafx.beans.property.SimpleObjectProperty;

public abstract class FactoryView<T extends FactoryBase<?,?>> implements Widget{

    protected SimpleObjectProperty<T> rootFactory= new SimpleObjectProperty<>();

    public void bind(T newRoot){
        rootFactory.set(newRoot);
    }

    public void unbind() {
        rootFactory.set(null);
    }
}
