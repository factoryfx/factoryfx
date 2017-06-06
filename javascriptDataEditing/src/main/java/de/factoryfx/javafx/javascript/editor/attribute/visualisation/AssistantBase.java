package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import javafx.application.Platform;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public abstract class AssistantBase<I,R> implements Consumer<I> {

    private final WeakReference<Consumer<R>> consumer;
    private BackgroundEvaluatingAssistant<I> backgroundEvaluator = new BackgroundEvaluatingAssistant<>();

    public AssistantBase(WeakReference<Consumer<R>> consumer) {
        this.consumer = consumer;
        backgroundEvaluator.start(input -> {
            Consumer<R> fConsumer = consumer.get();
            if (fConsumer == null) {
                dispose();
                return;
            }
            R result = process(input);
            Platform.runLater(() -> fConsumer.accept(result));
        });

    }

    protected abstract R process(I input);

    public final void dispose() {
        if (backgroundEvaluator != null) {
            backgroundEvaluator.stop();
            backgroundEvaluator = null;
        }
    }

    @Override
    protected final void finalize() throws Throwable {
        dispose();
    }

    @Override
    public final void accept(I newValue) {
        backgroundEvaluator.update(newValue);
    }
}
