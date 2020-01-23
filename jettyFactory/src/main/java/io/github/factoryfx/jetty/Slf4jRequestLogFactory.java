package io.github.factoryfx.jetty;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.primitive.IntegerAttribute;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

public class Slf4jRequestLogFactory<R extends FactoryBase<?, R>> extends FactoryBase<RequestLog, R> {


    public Slf4jRequestLogFactory() {
        configLifeCycle().setCreator(() -> {
            Slf4jRequestLog requestLog = new Slf4jRequestLog();
            requestLog.setExtended(false);
            requestLog.setLogTimeZone("GMT");
            requestLog.setLogDateFormat("");
            requestLog.setLogLatency(true);
            return requestLog;
        });
    }

}