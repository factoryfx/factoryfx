package de.factoryfx.javafx.view;

import java.util.Optional;

import de.factoryfx.javafx.view.container.ViewContainer;
import de.factoryfx.javafx.widget.CloseAwareWidget;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import org.controlsfx.glyphfont.Glyph;

public class View<T extends Widget> implements Widget {

    public final SimpleStringProperty title = new SimpleStringProperty();
    public final SimpleStringProperty dynamicAdditionalTitle = new SimpleStringProperty();

    public final SimpleObjectProperty<Glyph> icon = new SimpleObjectProperty<>();
    public final T widget;
    protected ViewContainer showViewStrategy;
    boolean isShowing;
    Node cachedContent;
    Optional<Runnable> closeListener = Optional.empty();

    public View(String title, ViewContainer showViewStrategy, T widget) {
        this.title.set(title);
        this.showViewStrategy = showViewStrategy;
        this.widget = widget;
    }

    public void close() {
        if (widget instanceof CloseAwareWidget) {
            ((CloseAwareWidget) widget).closeNotifier();
        }
        isShowing = false;
        showViewStrategy.close(this);
        closeListener.ifPresent(runnable -> runnable.run());
    }

    @Override
    public Node createContent() {
        return widget.createContent();
    }

    public Node getCachedContent() {
        if (cachedContent == null) {
            cachedContent = createContent();
        }
        return cachedContent;
    }

    public T getWidget() {
        return widget;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setCloseListener(Runnable closeListener) {
        this.closeListener = Optional.of(closeListener);
    }

    public void show() {
        showViewStrategy.show(this);
        isShowing = true;
    }

}
