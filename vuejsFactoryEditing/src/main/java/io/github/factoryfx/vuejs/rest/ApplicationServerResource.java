package io.github.factoryfx.vuejs.rest;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndNewMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.user.AuthorizedUser;
import de.factoryfx.user.UserManagement;
import de.factoryfx.user.persistent.UserFactory;


//@Path("factoryediting")
public class ApplicationServerResource<V,L,T extends FactoryBase<L,V>>  {


}
