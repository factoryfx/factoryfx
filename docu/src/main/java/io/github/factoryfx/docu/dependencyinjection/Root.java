package io.github.factoryfx.docu.dependencyinjection;

public class Root {
    private final Dependency dependency;
    public Root(Dependency dependency) {
        this.dependency=dependency;
        dependency.doX();
    }
}
