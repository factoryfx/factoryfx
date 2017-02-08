package de.factoryfx.server.rest;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.user.AuthorizedUser;
import de.factoryfx.user.UserManagement;
import de.factoryfx.user.persistent.UserFactory;

@Path("/")
public class ApplicationServerResource<V,L,T extends FactoryBase<L,V>> {

    private final ApplicationServer<L,V,T> applicationServer;
    private final UserManagement userManagement;
    private final Predicate<Optional<AuthorizedUser>> authorizedKeyUserEvaluator;

    public ApplicationServerResource(ApplicationServer<L,V,T> applicationServer, UserManagement userManagement, Predicate<Optional<AuthorizedUser>> authorizedKeyUserEvaluator) {
        this.applicationServer = applicationServer;
        this.userManagement = userManagement;
        this.authorizedKeyUserEvaluator = authorizedKeyUserEvaluator;
    }

    private Optional<AuthorizedUser> authenticate(UserAwareRequest<?> request){
        if (userManagement.authorisationRequired()){
            final Optional<AuthorizedUser> authorizedUser = userManagement.authenticate(request.user, request.passwordHash);
            if (!authorizedUser.isPresent()){
                throw new IllegalStateException("invalid user");
            }
            return authorizedUser;
        }
        return Optional.empty();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("updateCurrentFactory")
    public MergeDiffInfo updateCurrentFactory(UserAwareRequest<FactoryAndStorageMetadata> update) {
        authenticate(update);
        return new MergeDiffInfo(applicationServer.updateCurrentFactory(new FactoryAndStorageMetadata<>(update.request.root.internal().prepareUsableCopy(),update.request.metadata)));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("simulateUpdateCurrentFactory")
    public MergeDiffInfo simulateUpdateCurrentFactory(UserAwareRequest<FactoryAndStorageMetadata> request) {
        authenticate(request);
        return new MergeDiffInfo(applicationServer.simulateUpdateCurrentFactory(new FactoryAndStorageMetadata<>(request.request.root.internal().prepareUsableCopy(),request.request.metadata)));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("diff")
    public MergeDiffInfo getDiff(UserAwareRequest<StoredFactoryMetadata> request) {
        authenticate(request);
        return new MergeDiffInfo(applicationServer.getDiff(request.request));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("prepareNewFactory")
    public FactoryAndStorageMetadata prepareNewFactory(UserAwareRequest<Void> request) {
        authenticate(request);
        return applicationServer.prepareNewFactory();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactory")
    public Object getHistoryFactory(UserAwareRequest<String> request) {
        authenticate(request);
        return applicationServer.getHistoryFactory(request.request);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("historyFactoryList")
    public Collection<StoredFactoryMetadata> getHistoryFactoryList(UserAwareRequest<Void> request) {
        authenticate(request);
        return applicationServer.getHistoryFactoryList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("query")
    public V query(UserAwareRequest<V> request) {
        authenticate(request);
        return applicationServer.query(request.request);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("checkUser")
    public CheckUserResponse checkUser(UserAwareRequest<Void> request){
        return new CheckUserResponse(userManagement.authenticate(request.user,request.passwordHash).isPresent());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("userKey")
    public KeyResponse getUserKey(UserAwareRequest<Void> request){
        if (authorizedKeyUserEvaluator.test(authenticate(request))){
            return new KeyResponse(UserFactory.passwordKey);
        } else {
            return new KeyResponse("");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("userLocale")
    public UserLocaleResponse getUserLoacle(UserAwareRequest<Void> request){
        final Optional<AuthorizedUser> authenticate = authenticate(request);
        if (authenticate.isPresent()){
            return new UserLocaleResponse(authenticate.get().locale);
        } else {
            return new UserLocaleResponse(Locale.ENGLISH);
        }
    }
}
