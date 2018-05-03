package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.data.Data;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 *
 * @param <R> server root
 */
public interface FactoryAwareWidget<R> {

    Node init(R serverFactory);

    default Node update(R newFactory){
        return init(newFactory);
    }

    default SimpleObjectProperty<Data> selectedFactory(){
        return new SimpleObjectProperty<>();
    }
}
