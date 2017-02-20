package de.factoryfx.javafx.view;

import java.util.Optional;

import de.factoryfx.javafx.view.container.ViewsDisplayWidget;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class View implements Widget {

    public final SimpleStringProperty title = new SimpleStringProperty();

    public final Widget viewContent;
    protected ViewsDisplayWidget viewsDisplayWidget;
    boolean isShowing;
    Optional<Runnable> closeListener = Optional.empty();

    public View(String title, ViewsDisplayWidget viewsDisplayWidget, Widget viewContent) {
        this.title.set(title);
        this.viewsDisplayWidget = viewsDisplayWidget;
        this.viewContent = viewContent;
    }

    public void close() {
        viewContent.closeNotifier();
        isShowing = false;
        viewsDisplayWidget.close(this);
        closeListener.ifPresent(runnable -> runnable.run());
    }

    @Override
    public Node createContent() {
        final ScrollPane scrollPane = new ScrollPane(viewContent.createContent());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setCloseListener(Runnable closeListener) {
        this.closeListener = Optional.of(closeListener);
    }

    public void show() {
        viewsDisplayWidget.show(this);
        isShowing = true;
    }

}
