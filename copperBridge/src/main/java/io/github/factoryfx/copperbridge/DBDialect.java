package io.github.factoryfx.copperbridge;

import javax.sql.DataSource;

import org.copperengine.core.persistent.DatabaseDialect;
import org.copperengine.ext.wfrepo.classpath.ClasspathWorkflowRepository;

public class DBDialect {

    final DataSource dataSource;
    protected final ClasspathWorkflowRepository wfRepository;
    final DatabaseDialect dOra;

    public DBDialect(DataSource dataSource, ClasspathWorkflowRepository wfRepository, DatabaseDialect dOra) {
        this.dataSource = dataSource;
        this.wfRepository = wfRepository;
        this.dOra = dOra;
    }
}
