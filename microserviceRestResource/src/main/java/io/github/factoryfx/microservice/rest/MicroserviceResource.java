package io.github.factoryfx.microservice.rest;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.storage.StoredDataMetadata;
import io.github.factoryfx.microservice.common.CheckUserResponse;
import io.github.factoryfx.microservice.common.MicroserviceResourceApi;
import io.github.factoryfx.microservice.common.ResponseWorkaround;
import io.github.factoryfx.microservice.common.UserAwareRequest;
import io.github.factoryfx.microservice.common.UserLocaleResponse;
import io.github.factoryfx.microservice.common.VoidUserAwareRequest;
import io.github.factoryfx.server.Microservice;
import io.github.factoryfx.server.user.AuthorizedUser;
import io.github.factoryfx.server.user.UserManagement;

public class MicroserviceResource<R extends FactoryBase<?, R>> implements MicroserviceResourceApi<R> {

    protected final Microservice<?, R> microservice;
    private final UserManagement userManagement;

    public MicroserviceResource(Microservice<?, R> microservice, UserManagement userManagement) {
        this.microservice = microservice;
        this.userManagement = userManagement;
    }

    private Optional<AuthorizedUser> authenticate(UserAwareRequest<?> request) {
        if (userManagement.authorisationRequired()) {
            final Optional<AuthorizedUser> authorizedUser = userManagement.authenticate(request.user, request.passwordHash);
            if (authorizedUser.isEmpty()) {
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
    public FactoryUpdateLog<R> revert(UserAwareRequest<StoredDataMetadata> historyFactory) {
        authenticate(historyFactory);
        return microservice.revertTo(historyFactory.request, historyFactory.user);
    }

    private Function<String, Boolean> authenticateAndGetPermissionChecker(UserAwareRequest<?> request) {
        final Optional<AuthorizedUser> authenticate = authenticate(request);
        Function<String, Boolean> permissionChecker = (permission) -> true;
        if (authenticate.isPresent()) {
            permissionChecker = (permission) -> authenticate.get().checkPermissionValid(permission);
        }
        return permissionChecker;
    }

    @Override
    public MergeDiffInfo<R> simulateUpdateCurrentFactory(UserAwareRequest<DataUpdate<R>> request) {
        request.request.permissionChecker = authenticateAndGetPermissionChecker(request);
        request.request.root.internal().finalise();
        return microservice.simulateUpdateCurrentFactory(request.request);
    }

    @Override
    public MergeDiffInfo<R> getDiff(UserAwareRequest<StoredDataMetadata> request) {
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
    public Collection<StoredDataMetadata> getHistoryFactoryList(VoidUserAwareRequest request) {
        authenticate(request);
        return microservice.getHistoryFactoryList();
    }

    @Override
    public CheckUserResponse checkUser(VoidUserAwareRequest request) {
        return userManagement.authenticate(request.user, request.passwordHash)
                             .map(s -> new CheckUserResponse(true, s.getPermissions()))
                             .orElseGet(() -> new CheckUserResponse(false, Collections.emptyList()));
    }

    @Override
    public UserLocaleResponse getUserLocale(VoidUserAwareRequest request) {
        final Optional<AuthorizedUser> authenticate = authenticate(request);
        return authenticate.map(authorizedUser -> new UserLocaleResponse(authorizedUser.getLocale())).orElseGet(() -> new UserLocaleResponse(Locale.ENGLISH));
    }

}
