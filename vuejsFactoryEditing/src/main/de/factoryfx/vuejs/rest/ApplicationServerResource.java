package de.factoryfx.server.rest;

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


@Path("factoryediting")
public class ApplicationServerResource<V,L,T extends FactoryBase<L,V>>  {

    private final ApplicationServer<V,L,T> applicationServer;
    private final UserManagement userManagement;
    private final Predicate<Optional<AuthorizedUser>> authorizedKeyUserEvaluator;

    public ApplicationServerResource(ApplicationServer<V,L,T> applicationServer, UserManagement userManagement, Predicate<Optional<AuthorizedUser>> authorizedKeyUserEvaluator) {
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
    public FactoryUpdateLog updateCurrentFactory(UserAwareRequest<UpdateCurrentFactoryRequest> update) {
        Function<String, Boolean> permissionChecker = authenticateAndGetPermissionChecker(update);
        return applicationServer.updateCurrentFactory(new FactoryAndNewMetadata<>(update.request.factoryUpdate.root.internal().prepareUsableCopy(), update.request.factoryUpdate.metadata), update.user, update.request.comment, permissionChecker);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("revert")
    public FactoryUpdateLog revert(UserAwareRequest<StoredFactoryMetadata> historyFactory) {
        authenticate(historyFactory);
        return applicationServer.revertTo(historyFactory.request,historyFactory.user);
    }

    private Function<String, Boolean> authenticateAndGetPermissionChecker(UserAwareRequest<?> request) {
        final Optional<AuthorizedUser> authenticate = authenticate(request);
        Function<String,Boolean> permissionChecker = (permission)->true;
        if (authenticate.isPresent()){
            permissionChecker = (permission)->authenticate.get().checkPermissionValid(permission);
        }
        return permissionChecker;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("simulateUpdateCurrentFactory")
    public MergeDiffInfo simulateUpdateCurrentFactory(UserAwareRequest<FactoryAndNewMetadata> request) {
        Function<String, Boolean> permissionChecker = authenticateAndGetPermissionChecker(request);
        return applicationServer.simulateUpdateCurrentFactory(new FactoryAndNewMetadata<>(request.request.root.internal().prepareUsableCopy(), request.request.metadata), permissionChecker);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("diff")
    public MergeDiffInfo getDiff(UserAwareRequest<StoredFactoryMetadata> request) {
        authenticate(request);
        return applicationServer.getDiffToPreviousVersion(request.request);

    }



    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("prepareNewFactory")
    public FactoryAndNewMetadata prepareNewFactory(UserAwareRequest<Void> request) {
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
    public UserLocaleResponse getUserLocale(UserAwareRequest<Void> request){
        final Optional<AuthorizedUser> authenticate = authenticate(request);
        return authenticate.map(authorizedUser -> new UserLocaleResponse(authorizedUser.getLocale())).orElseGet(() -> new UserLocaleResponse(Locale.ENGLISH));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("diffForFactory")
    public DiffForFactoryResponse getDiffHistoryForFactory(UserAwareRequest<String> request){
        authenticate(request);
        final DiffForFactoryResponse diffForFactoryResponse = new DiffForFactoryResponse();
        diffForFactoryResponse.diffs=applicationServer.getDiffHistoryForFactory(request.request);
        return diffForFactoryResponse;

    }

}
