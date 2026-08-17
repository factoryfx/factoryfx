package io.github.factoryfx.javafx.factoryviewmanager;

import javafx.scene.Node;

/**
 * Base class for {@link FactoryAwareWidget}s that separates the widget lifecycle into three hooks,
 * so that opening a view is never blocked by the server-factory download and re-opening a view is cheap:
 *
 * <ul>
 *   <li>{@link #initContent()} — build the static UI. Called once per widget instance, on the first
 *       {@link #createContent()}. The framework embeds the returned node immediately, before the
 *       factory has been downloaded.</li>
 *   <li>{@link #refreshData()} — load/refresh data that does not come from the factory (e.g. REST calls).
 *       Called on every {@link #createContent()} (i.e. every time the view is opened) and again after
 *       every new factory version (save/update). Started in parallel with the factory download on first
 *       open. Implementations should run blocking work off the JavaFX thread (e.g. via
 *       {@code LongRunningActionExecutor}) and apply results with {@code Platform.runLater}.</li>
 *   <li>{@link #applyFactory(Object)} — apply configuration from the factory tree. Called once per new
 *       root instance (initial load and after every save/update), never again for the same instance.</li>
 * </ul>
 *
 * Typical shapes:
 * <ul>
 *   <li>View shows only generic (non-factory) data: override {@code initContent} and {@code refreshData}.
 *       (If the factory is not needed at all, prefer a plain {@code Widget} instead of this class.)</li>
 *   <li>View mixes generic data with a few configuration values: additionally override {@code applyFactory}
 *       and store the values in observable properties so bindings react when the configuration arrives.</li>
 *   <li>View UI is built entirely from the factory tree: override {@code initContent} with a cheap skeleton
 *       and rebuild in {@code applyFactory}; leave {@code refreshData} empty. Consider building expensive
 *       sub-content (e.g. tab contents) lazily on first selection.</li>
 * </ul>
 *
 * Note: until the first {@link #applyFactory(Object)} call, {@link #getCurrentFactory()} returns null and
 * factory-dependent actions should be disabled (bind them to a property set in {@code applyFactory}).
 *
 * @param <R> server root
 */
public abstract class LifecycleFactoryAwareWidget<R> implements FactoryAwareWidget<R> {

    private Node content;
    private R currentFactory;

    @Override
    public final Node createContent() {
        if (content == null) {
            content = initContent();
        }
        refreshData();
        return content;
    }

    @Override
    public final void edit(R rootFactory) {
        if (rootFactory == currentFactory) {
            return; //defensive: also correct on framework versions that re-deliver the same root per view open
        }
        boolean firstDelivery = currentFactory == null;
        currentFactory = rootFactory;
        applyFactory(rootFactory);
        if (!firstDelivery) {
            //new factory version after a save/update: server-side data may have changed as well.
            //on the first delivery createContent() has already triggered the refresh.
            refreshData();
        }
    }

    /** Builds the static UI. Called exactly once, before the factory is available. */
    protected abstract Node initContent();

    /** Applies configuration from the factory tree. Called once per new root instance. Default: no-op. */
    protected void applyFactory(R rootFactory) {
    }

    /** Loads/refreshes non-factory data. Called on every view open and after every new factory version. Default: no-op. */
    protected void refreshData() {
    }

    /** The most recently delivered root factory, or null before the first {@link #applyFactory(Object)}. */
    protected final R getCurrentFactory() {
        return currentFactory;
    }

    /** The node returned by {@link #initContent()}, or null before the first {@link #createContent()}. */
    protected final Node getContent() {
        return content;
    }
}
