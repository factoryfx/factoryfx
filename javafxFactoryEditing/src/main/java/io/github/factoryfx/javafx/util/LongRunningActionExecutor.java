package io.github.factoryfx.javafx.util;

import java.util.concurrent.Executor;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class LongRunningActionExecutor implements Executor {
    private final StackPane target=new StackPane();

    public LongRunningActionExecutor() {

    }

    private Node createProgressIndicator() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setMaxHeight(350);
        indicator.setMaxWidth(350);

        BorderPane borderPane = new BorderPane();
        BorderPane.setMargin(indicator, new Insets(5));
        borderPane.setCenter(indicator);
        borderPane.setStyle("-fx-background-color: rgba(230,230,230,0.7);");
        return borderPane;
    }

    //** execute with progress dialog in background */
    public void execute(final Runnable runnable, final String text) {
        Thread backgroundThread = new Thread(() -> {
            final Node progressIndicator = createProgressIndicator();
            final Label label = new Label(text);
            try {
                Platform.runLater(() -> {
                    target.getChildren().add(progressIndicator);
                    FadeTransition ft = new FadeTransition(Duration.millis(500), progressIndicator);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.play();
                    label.setWrapText(true);
                    target.getChildren().add(label);
                });
                runnable.run();
            } catch (Exception exception){
                if (Thread.getDefaultUncaughtExceptionHandler()!=null){
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(),exception);
                } else {
                    exception.printStackTrace();
                }
            } finally {
                Platform.runLater(() -> {
                    target.getChildren().remove(progressIndicator);
                    target.getChildren().remove(label);
                });
            }
        });
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    @Override
    public void execute(final Runnable runnable) {
        this.execute(runnable,"");
    }

    public Parent wrap(Pane root) {
        target.getChildren().add(root);
        return target;
    }
}
