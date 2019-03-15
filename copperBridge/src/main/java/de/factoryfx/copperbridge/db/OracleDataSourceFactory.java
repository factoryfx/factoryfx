package de.factoryfx.copperbridge.db;

import java.util.List;

import javax.sql.DataSource;

import org.copperengine.core.EngineIdProvider;
import org.copperengine.core.persistent.OracleDialect;
import org.copperengine.ext.wfrepo.classpath.ClasspathWorkflowRepository;

import de.factoryfx.copperbridge.DBDialect;
import de.factoryfx.copperbridge.EngineIdProviderFactory;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public abstract class OracleDataSourceFactory<R extends FactoryBase<?, R>> extends FactoryBase<DBDialect, R> {

    public final BooleanAttribute multiEngineMode = new BooleanAttribute().labelText("MultiEngineMode");

    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<EngineIdProvider, EngineIdProviderFactory<R>> engineIdProviderFactory =
        FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(EngineIdProviderFactory.class)).labelText("EngineId provider");

    public abstract DataSource getDataSource();

    public abstract List<String> getWorkflowClassPaths();

    public OracleDataSourceFactory() {
        configLifeCycle().setCreator(() -> {
            OracleDialect dOra = new OracleDialect();
            ClasspathWorkflowRepository wfRepository = new ClasspathWorkflowRepository(getWorkflowClassPaths());
            dOra.setWfRepository(wfRepository);
            dOra.setEngineIdProvider(engineIdProviderFactory.instance());
            dOra.setMultiEngineMode(multiEngineMode.get());
            return new DBDialect(getDataSource(), wfRepository,  dOra);
        });
    }

}
