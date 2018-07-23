package de.factoryfx.microservice.rest;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.data.storage.DataAndNewMetadata;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import de.factoryfx.factory.log.FactoryUpdateLog;
import de.factoryfx.microservice.common.*;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.user.AuthorizedUser;
import de.factoryfx.server.user.UserManagement;
import de.factoryfx.server.user.persistent.UserFactory;



/**
 *  https://stackoverflow.com/questions/17000193/can-we-have-more-than-one-path-annotation-for-same-rest-method
 *  3 Paths for compatibility
 *  microservice is new one
 */
public class MicroserviceResource<V,R extends FactoryBase<?,V,R>,S> implements MicroserviceResourceApi<V,R,S> {

    private final Microservice<V,R,S> microservice;
    private final UserManagement userManagement;
    private final Predicate<Optional<AuthorizedUser>> authorizedKeyUserEvaluator;
    private final Supplier<V> emptyVisitorCreator;
    private final FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup;

    public MicroserviceResource(Microservice<V,R,S> microservice, UserManagement userManagement, Predicate<Optional<AuthorizedUser>> authorizedKeyUserEvaluator) {
        this(microservice,userManagement,authorizedKeyUserEvaluator,null,null);
    }

    public MicroserviceResource(Microservice<V,R,S> microservice, UserManagement userManagement, Predicate<Optional<AuthorizedUser>> authorizedKeyUserEvaluator, Supplier<V> emptyVisitorCreator, FactoryTreeBuilderBasedAttributeSetup<R> factoryTreeBuilderBasedAttributeSetup) {
        this.microservice = microservice;
        this.userManagement = userManagement;
        this.authorizedKeyUserEvaluator = authorizedKeyUserEvaluator;
        this.emptyVisitorCreator= emptyVisitorCreator;
        this.factoryTreeBuilderBasedAttributeSetup = factoryTreeBuilderBasedAttributeSetup;
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
    public FactoryUpdateLog updateCurrentFactory(UserAwareRequest<UpdateCurrentFactoryRequest> update) {
        Function<String, Boolean> permissionChecker = authenticateAndGetPermissionChecker(update);
        return microservice.updateCurrentFactory(new DataAndNewMetadata<>(update.request.factoryUpdate.root.internal().addBackReferences(), update.request.factoryUpdate.metadata), update.user, update.request.comment, permissionChecker);
    }

    @Override
    public FactoryUpdateLog revert(UserAwareRequest<StoredDataMetadata> historyFactory) {
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
    public MergeDiffInfo simulateUpdateCurrentFactory(UserAwareRequest<DataAndNewMetadata> request) {
        Function<String, Boolean> permissionChecker = authenticateAndGetPermissionChecker(request);
        return microservice.simulateUpdateCurrentFactory(new DataAndNewMetadata<>(request.request.root.internal().addBackReferences(), request.request.metadata), permissionChecker);
    }

    @Override
    public MergeDiffInfo getDiff(UserAwareRequest<StoredDataMetadata> request) {
        authenticate(request);
        return microservice.getDiffToPreviousVersion(request.request);
    }

    @Override
    public DataAndNewMetadata prepareNewFactory(UserAwareRequest<Void> request) {
        authenticate(request);
        return microservice.prepareNewFactory();
    }

    @Override
    public ResponseWorkaround<R> getHistoryFactory(UserAwareRequest<String> request) {
        authenticate(request);
        return new ResponseWorkaround<>(microservice.getHistoryFactory(request.request));
    }

    @Override
    public Collection<StoredDataMetadata<S>> getHistoryFactoryList(UserAwareRequest<Void> request) {
        authenticate(request);
        return microservice.getHistoryFactoryList();
    }

    @Override
    public ResponseWorkaround<V> query(UserAwareRequest<V> request) {
        authenticate(request);
        return new ResponseWorkaround<>(microservice.query(request.request));
    }

    @Override
    public ResponseWorkaround<V> queryReadOnly(UserAwareRequest<Void> request) {
        authenticate(request);
        return new ResponseWorkaround<>(microservice.query(emptyVisitorCreator.get()));
    }

    @Override
    public CheckUserResponse checkUser(UserAwareRequest<Void> request){
        return new CheckUserResponse(userManagement.authenticate(request.user,request.passwordHash).isPresent());
    }

    @Override
    public KeyResponse getUserKey(UserAwareRequest<Void> request){
        if (authorizedKeyUserEvaluator.test(authenticate(request))){
            return new KeyResponse(UserFactory.passwordKey);
        } else {
            return new KeyResponse("");
        }
    }

    @Override
    public UserLocaleResponse getUserLocale(UserAwareRequest<Void> request){
        final Optional<AuthorizedUser> authenticate = authenticate(request);
        return authenticate.map(authorizedUser -> new UserLocaleResponse(authorizedUser.getLocale())).orElseGet(() -> new UserLocaleResponse(Locale.ENGLISH));
    }

}
