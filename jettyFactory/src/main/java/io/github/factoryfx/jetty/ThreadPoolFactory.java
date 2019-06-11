package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

public class ThreadPoolFactory<R extends FactoryBase<?, R>> extends FactoryBase<ThreadPool, R> {

    public final IntegerAttribute poolSize = new IntegerAttribute().labelText("Pool size");

    public ThreadPoolFactory() {
        configLifeCycle().setCreator(() -> new ExecutorThreadPool(poolSize.get()));
    }

}