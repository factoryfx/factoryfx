package io.github.factoryfx.data.attribute;

import java.util.function.Consumer;

//workaround to not depend on javafx
public interface RunLaterAble {
    //** so we don't need to initialise javax toolkit in test, Platform.runLater(runnable);*/
    void setRunlaterExecutor(Consumer<Runnable> runlaterExecutor);
}
