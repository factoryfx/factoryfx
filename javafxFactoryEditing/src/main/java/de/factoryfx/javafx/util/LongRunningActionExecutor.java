package de.factoryfx.javafx.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class LongRunningActionExecutor {
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

    public StackPane getStackPane() {
        return target;
    }

    //** execute with progress dialog in background */
    public void execute(final Runnable runnable, final String text) {
        Thread th = new Thread() {
            @Override
            public void run() {
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
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(),exception);
                } finally {
                    Platform.runLater(() -> {
                        target.getChildren().remove(progressIndicator);
                        target.getChildren().remove(label);
                    });
                }
            }
        };
        th.setDaemon(true);
        th.start();
    }

    public void execute(final Runnable runnable) {
        this.execute(runnable,"");
    }

}
