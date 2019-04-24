package io.github.factoryfx.docu.mock;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FactoryTreeBuilderRule<L, R extends FactoryBase<L, R>, S> implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

    private class FactoryIdentifier {
        private final Class<R> clazz;
        private final String name;

        public FactoryIdentifier(Class<R> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }
    }

    protected final FactoryTreeBuilder<L,R,S> builder;
    private final List<FactoryIdentifier> branches = new ArrayList<>();
    private final List<FactoryIdentifier> prototypeBranches = new ArrayList<>();

    private boolean runAll = false;

    public FactoryTreeBuilderRule(FactoryTreeBuilder<L, R, S> builder) {
        this.builder = builder;
    }

    public <L0, F0 extends FactoryBase<L0, R>> void mock(Class<F0> factoryClazz, Function<F0, L0> replacementCreator) {
        mock(factoryClazz, null, replacementCreator);
    }

    @SuppressWarnings("unchecked")
    public <L0, F0 extends FactoryBase<L0, R>> void mock(Class<F0> factoryClazz, String name, Function<F0, L0> replacementCreator) {
        builder.branch().select(factoryClazz, name).factory().utility().mock(f -> replacementCreator.apply((F0) f));
    }

    public <L0, F0 extends FactoryBase<L0, R>> L0 get(Class<F0> factoryClazz) {
        return get(factoryClazz, null);
    }

    @SuppressWarnings("unchecked")
    public <L0, F0 extends FactoryBase<L0, R>> L0 get(Class<F0> factoryClazz, String name) {
        branches.add(new FactoryIdentifier((Class<R>) factoryClazz, name));
        return builder.branch().select(factoryClazz, name).instance();
    }

    public <L0, F0 extends FactoryBase<L0, R>> Set<L0> getPrototypeInstances(Class<F0> factoryClazz) {
        return getPrototypeInstances(factoryClazz, null);
    }

    @SuppressWarnings("unchecked")
    public <L0, F0 extends FactoryBase<L0, R>> Set<L0> getPrototypeInstances(Class<F0> factoryClazz, String name) {
        prototypeBranches.add(new FactoryIdentifier((Class<R>) factoryClazz, name));
        return builder.branch().selectPrototype(factoryClazz, name).stream().map(b -> b.instance()).collect(Collectors.toSet());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (!runAll) {
            before();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (!runAll) {
            after();
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        runAll = true;
        before();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        try {
            after();
        } finally {
            runAll = false;
        }
    }

    private void before() {
        branches.forEach(b -> {
            builder.branch().select(b.clazz, b.name).start();
        });
        prototypeBranches.forEach(b -> builder.branch().selectPrototype(b.clazz, b.name).forEach(i -> i.start()));
    }

    private void after() {
        branches.forEach(b -> {
            builder.branch().select(b.clazz, b.name).stop();
        });
        prototypeBranches.forEach(b -> builder.branch().selectPrototype(b.clazz, b.name).forEach(i -> i.stop()));
    }
}
