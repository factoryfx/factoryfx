package io.github.factoryfx.example.server.testutils;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import org.junit.jupiter.api.extension.*;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FactoryTreeBuilderRule<L, R extends FactoryBase<L, R>, S> implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

    protected final FactoryTreeBuilder<L,R,S> builder;
    private final Consumer<FactoryTreeBuilderRule<L,R,S>> preStart;

    private boolean runAll = false;

    public FactoryTreeBuilderRule(FactoryTreeBuilder<L, R, S> builder, Consumer<FactoryTreeBuilderRule<L, R, S>> preStart) {
        this.preStart = preStart;
        this.builder = builder;
    }

    public <L0, F0 extends FactoryBase<L0, R>> void mock(Class<F0> factoryClazz, Function<F0, L0> replacementCreator) {
        mock(factoryClazz, null, replacementCreator);
    }

    public <L0, F0 extends FactoryBase<L0, R>> void mock(Class<F0> factoryClazz, String name, Function<F0, L0> replacementCreator) {
        builder.branch().select(factoryClazz, name).mock(f -> replacementCreator.apply(f));
    }

    public <L0, F0 extends FactoryBase<L0, R>> L0 get(Class<F0> factoryClazz) {
        return get(factoryClazz, null);
    }

    public <L0, F0 extends FactoryBase<L0, R>> L0 get(Class<F0> factoryClazz, String name) {
        return builder.branch().select(factoryClazz, name).instance();
    }

    public <L0, F0 extends FactoryBase<L0, R>> Set<L0> getPrototypeInstances(Class<F0> factoryClazz) {
        return getPrototypeInstances(factoryClazz, null);
    }

    public <L0, F0 extends FactoryBase<L0, R>> Set<L0> getPrototypeInstances(Class<F0> factoryClazz, String name) {
        return builder.branch().selectPrototype(factoryClazz, name).stream().map(b -> b.instance()).collect(Collectors.toSet());
    }

    public <L0, F0 extends FactoryBase<L0, R>> F0 getFactory(Class<F0> factoryClazz) {
        return getFactory(factoryClazz, null);
    }

    public <L0, F0 extends FactoryBase<L0, R>> F0 getFactory(Class<F0> factoryClazz, String name) {
        return builder.branch().select(factoryClazz, name).factory();
    }

    private void before() {
        if (preStart != null) {
            preStart.accept(this);
        }
        builder.branch().select(builder.buildTree().getClass()).start();
    }

    private void after() {
        builder.branch().select(builder.buildTree().getClass()).stop();
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
    }}
