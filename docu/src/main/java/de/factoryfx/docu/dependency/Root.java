package de.factoryfx.docu.dependency;

public class Root {
    private final Dependency dependency;
    public Root(Dependency dependency) {
        this.dependency=dependency;
        dependency.doX();
    }
}
