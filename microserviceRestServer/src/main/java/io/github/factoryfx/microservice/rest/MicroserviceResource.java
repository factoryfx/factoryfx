package io.github.factoryfx.microservice.rest;

import java.util.*;
import java.util.function.Function;

import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.server.user.AuthorizedUser;
import io.github.factoryfx.server.user.UserManagement;
import io.github.factoryfx.microservice.common.*;

public class MicroserviceResource<R extends FactoryBase<?,R>,S> implements MicroserviceResourceApi<R,S> {

    protected final Microservice<?,R,S> microservice;
    private final UserManagement userManagement;

    public MicroserviceResource(Microservice<?,R,S> microservice, UserManagement userManagement) {
        this.microservice = microservice;
        this.userManagement = userManagement;
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

    @Override
    public FactoryUpdateLog<R> updateCurrentFactory(UserAwareRequest<DataUpdate<R>> update) {
        update.request.permissionChecker = authenticateAndGetPermissionChecker(update);
        update.request.root.internal().finalise();
        return microservice.updateCurrentFactory(update.request);
    }

    @Override
    public FactoryUpdateLog<R> revert(UserAwareRequest<StoredDataMetadata<S>> historyFactory) {
        authenticate(historyFactory);
        return microservice.revertTo(historyFactory.request,historyFactory.user);
    }

    private Function<String, Boolean> authenticateAndGetPermissionChecker(UserAwareRequest<?> request) {
        final Optional<AuthorizedUser> authenticate = authenticate(request);
        Function<String,Boolean> permissionChecker = (permission)->true;
        if (authenticate.isPresent()){
            permissionChecker = (permission)->authenticate.get().checkPermissionValid(permission);
        }
        return permissionChecker;
    }

    @Override
    public MergeDiffInfo<R> simulateUpdateCurrentFactory(UserAwareRequest<DataUpdate<R>> request) {
        request.request.permissionChecker= authenticateAndGetPermissionChecker(request);
        request.request.root.internal().finalise();
        return microservice.simulateUpdateCurrentFactory(request.request);
    }

    @Override
    public MergeDiffInfo<R> getDiff(UserAwareRequest<StoredDataMetadata<S>> request) {
        authenticate(request);
        return microservice.getDiffToPreviousVersion(request.request);
    }

    @Override
    public DataUpdate<R> prepareNewFactory(VoidUserAwareRequest request) {
        authenticate(request);
        return microservice.prepareNewFactory();
    }

    @Override
    public ResponseWorkaround<R> getHistoryFactory(UserAwareRequest<String> request) {
        authenticate(request);
        return new ResponseWorkaround<>(microservice.getHistoryFactory(request.request));
    }

    @Override
    public Collection<StoredDataMetadata<S>> getHistoryFactoryList(VoidUserAwareRequest request) {
        authenticate(request);
        return microservice.getHistoryFactoryList();
    }

    @Override
    public CheckUserResponse checkUser(VoidUserAwareRequest request){
        return new CheckUserResponse(userManagement.authenticate(request.user,request.passwordHash).isPresent());
    }

    @Override
    public UserLocaleResponse getUserLocale(VoidUserAwareRequest request){
        final Optional<AuthorizedUser> authenticate = authenticate(request);
        return authenticate.map(authorizedUser -> new UserLocaleResponse(authorizedUser.getLocale())).orElseGet(() -> new UserLocaleResponse(Locale.ENGLISH));
    }

}
