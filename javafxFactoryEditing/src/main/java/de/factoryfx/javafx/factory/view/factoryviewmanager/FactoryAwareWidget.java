package de.factoryfx.javafx.factory.view.factoryviewmanager;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.data.widget.Widget;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 *
 * @param <R> server root
 */
public interface FactoryAwareWidget<R> extends Widget {

    void edit(R newFactory);

    default SimpleObjectProperty<Data> selectedFactory(){
        return new SimpleObjectProperty<>();
    }


}
