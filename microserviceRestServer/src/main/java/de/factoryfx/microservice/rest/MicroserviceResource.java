package de.factoryfx.microservice.rest;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.storage.DataUpdate;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.microservice.common.*;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.user.AuthorizedUser;
import de.factoryfx.server.user.UserManagement;

public class MicroserviceResource<V,R extends FactoryBase<?,V,R>,S> implements MicroserviceResourceApi<V,R,S> {

    private final Microservice<V,?,R,S> microservice;
    private final UserManagement userManagement;

    private final Supplier<V> emptyVisitorCreator;

    public MicroserviceResource(Microservice<V,?,R,S> microservice, UserManagement userManagement) {
        this(microservice,userManagement,null);
    }

    public MicroserviceResource(Microservice<V,?,R,S> microservice, UserManagement userManagement, Supplier<V> emptyVisitorCreator) {
        this.microservice = microservice;
        this.userManagement = userManagement;
        this.emptyVisitorCreator= emptyVisitorCreator;
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
        update.request.root.internal().addBackReferences();
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
        request.request.root.internal().addBackReferences();
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
    public ResponseWorkaround<V> query(UserAwareRequest<V> request) {
        authenticate(request);
        return new ResponseWorkaround<>(microservice.query(request.request));
    }

    @Override
    public ResponseWorkaround<V> queryReadOnly(VoidUserAwareRequest request) {
        authenticate(request);
        return new ResponseWorkaround<>(microservice.query(emptyVisitorCreator.get()));
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
