package io.github.factoryfx.jetty;

import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.RequestLog;

import io.github.factoryfx.factory.FactoryBase;

public class Slf4jRequestLogFactory<R extends FactoryBase<?, R>> extends FactoryBase<RequestLog, R> {

    public Slf4jRequestLogFactory() {
        configLifeCycle().setCreator(() -> {
            CustomRequestLog requestLog = new CustomRequestLog();
            // requestLog.setExtended(false);
            // requestLog.setLogTimeZone("GMT");
            // requestLog.setLogDateFormat("");
            // requestLog.setLogLatency(true);
            return requestLog;
        });
    }

}