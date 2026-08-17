package io.github.factoryfx.javafx.factoryviewmanager;

import io.github.factoryfx.javafx.widget.Widget;

/**
 * A {@link Widget} that additionally receives the server root factory.
 *
 * Lifecycle (see {@link FactoryEditView}):
 * <ul>
 *   <li>{@link #createContent()} is called on every view open, BEFORE the factory is available.
 *       Return the UI immediately (build it lazily on the first call) and start loading any
 *       non-factory data here, so it runs in parallel with the factory download.</li>
 *   <li>{@link #edit(Object)} is called at most once per root instance: after the initial factory
 *       load and after every save/update that produces a new root. It is NOT called again when the
 *       view is merely re-opened with an unchanged factory.</li>
 * </ul>
 *
 * Prefer extending {@link LifecycleFactoryAwareWidget}, which splits this contract into
 * {@code initContent()}/{@code refreshData()}/{@code applyFactory()} hooks.
 *
 * @param <R> server root
 */
public interface FactoryAwareWidget<R> extends Widget {

    /** Applies the (new) root factory. Called once per new root instance, always after {@link #createContent()}. */
    void edit(R rootFactory);
}
