package de.factoryfx.javafx.view.container;

import de.factoryfx.javafx.view.View;

public abstract class ViewContainer {
    public static final int HISTORY_LIMIT = 10;
    protected CloseListener onCloseListener;

    public void close(View<?> view) {
        closeImpl(view);

        if (onCloseListener != null) {
            onCloseListener.closed(view);
        }
    }

    protected abstract void closeImpl(View<?> view);

    /**
     * called if view is closed
     */
    public void setOnCloseListener(CloseListener closeListener) {
        onCloseListener = closeListener;
    }

    public void show(View<?> view) {
        showImpl(view);
    }

    protected abstract void showImpl(View<?> view);

    public interface CloseListener {
        void closed(View<?> view);
    }

}