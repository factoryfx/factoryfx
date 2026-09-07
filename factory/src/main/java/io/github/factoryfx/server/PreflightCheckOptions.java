package io.github.factoryfx.server;

/**
 * options for {@link Microservice#preflightCheck(PreflightCheckOptions)}, all checks beyond the base checks are off by default
 */
public class PreflightCheckOptions {
    boolean includeHistory;
    boolean createLiveObjects;

    /**
     * additionally check that every history configuration can be loaded (slower)
     * @return this
     */
    public PreflightCheckOptions includeHistory() {
        this.includeHistory = true;
        return this;
    }

    /**
     * additionally create all liveObjects (the factories' create phase) WITHOUT starting them.<br>
     * the created objects are discarded afterwards (no start, no destroy). this assumes the factory lifecycle
     * contract that external resources like ports are claimed in start and released in destroy, so
     * created-but-never-started liveObjects hold no external resources.
     * @return this
     */
    public PreflightCheckOptions createLiveObjects() {
        this.createLiveObjects = true;
        return this;
    }
}
