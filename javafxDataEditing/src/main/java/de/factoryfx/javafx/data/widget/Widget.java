package de.factoryfx.javafx.data.widget;

public interface Widget extends CloseAwareWidget {
    default void closeNotifier(){
        //nothing by default Widget don't handle close
    }
}
