package de.factoryfx.javafx.data.widget;

public class CloseAwareFxmlWidget<C extends CloseAwareFxmlController> extends FxmlWidget<C> implements CloseAwareWidget {

    public CloseAwareFxmlWidget(C controller) {
        super(controller);
    }

    @Override
    public void closeNotifier() {
        getController().closeNotifier();
    }
}
