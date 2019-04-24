package io.github.factoryfx.factory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Throwables;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import io.github.factoryfx.factory.exception.ExceptionResponseAction;
import io.github.factoryfx.factory.exception.FactoryExceptionHandler;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import org.slf4j.LoggerFactory;

/**
 * Manage application lifecycle (start,stop,update)
 *
 * @param <R> Root
 */
public class FactoryManager<L,R extends FactoryBase<L,R>> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FactoryManager.class);

    private RootFactoryWrapper<R> currentFactoryRoot;
    private final FactoryExceptionHandler factoryExceptionHandler;

    public FactoryManager(FactoryExceptionHandler factoryExceptionHandler) {
        this.factoryExceptionHandler = factoryExceptionHandler;
        if (factoryExceptionHandler instanceof AllOrNothingFactoryExceptionHandler){
            logger.warn("only AllOrNothingFactoryExceptionHandler is set therefore no exception will be logged. Usually this setup is wrong and the handler should be wrapped with LoggingFactoryExceptionHandler");
        }
    }

    @SuppressWarnings("unchecked")
    public FactoryUpdateLog<R> update(R commonVersion , R newVersion, Function<String,Boolean> permissionChecker){
        if (currentFactoryRoot==null) {
            throw new IllegalStateException("update on a not started manager");
        }

        Collection<FactoryBase<?,R>> previousFactories = currentFactoryRoot.getFactoriesInDestroyOrder();
        previousFactories.forEach((f) -> f.internal().resetLog());
        R previousFactoryCopyRoot = currentFactoryRoot.copy();

        FactoryBase<?,?> factoryBaseInFocus=null;//for better exception reporting

        MergeDiffInfo<R> mergeDiff = currentFactoryRoot.merge(commonVersion, newVersion, permissionChecker);
        long totalUpdateDuration = 0;
        List<FactoryBase<?,R>> removed = new ArrayList<>();

        if (mergeDiff.successfullyMerged()) {
            try {
                final Collection<FactoryBase<?,R>> currentFactoriesAfterMerge = currentFactoryRoot.collectChildFactories();
                removed = getRemovedFactories(previousFactories, new HashSet<>(currentFactoriesAfterMerge));

                long start = System.nanoTime();
                determineRecreationNeedFromRoot(previousFactoryCopyRoot);

                //attention lifecycle method call order is important

                final Collection<FactoryBase<?,R>> factoriesInCreateAndStartOrder = currentFactoryRoot.getFactoriesInCreateAndStartOrder();
                for (FactoryBase<?,?> factory : factoriesInCreateAndStartOrder) {
                    factoryBaseInFocus=factory;
                    factory.internal().instance();
                    //createWithExceptionHandling(factory, new RootFactoryWrapper<>(previousFactoryCopyRoot), currentFactoryRoot);
                }

                for (FactoryBase<?,R> factory : currentFactoriesAfterMerge) {
                    factoryBaseInFocus=factory;
                    factory.internal().destroyUpdated();
                }
                for (FactoryBase<?,R> factory : removed) {
                    factoryBaseInFocus=factory;
                    factory.internal().destroyRemoved();
                }


                //factoriesInCreateAndStartOrder.forEach(this::startWithExceptionHandling);
                for (FactoryBase<?,R> factory : factoriesInCreateAndStartOrder) {
                    factoryBaseInFocus=factory;
                    factory.internal().start();
                }
                totalUpdateDuration = System.nanoTime() - start;
                logger.info(currentFactoryRoot.logUpdateDisplayTextDeep());
                return new FactoryUpdateLog<>(currentFactoryRoot.createFactoryLogTree(), removed.stream().map(r->r.internal().createFactoryLogEntry()).collect(Collectors.toSet()),mergeDiff,totalUpdateDuration,null);
            } catch(Exception e){
                factoryExceptionHandler.updateException(e,factoryBaseInFocus,new ExceptionResponseAction(this, new RootFactoryWrapper(previousFactoryCopyRoot),currentFactoryRoot,removed));
                return new FactoryUpdateLog<>(Throwables.getStackTraceAsString(e));
            }
        } else {
            return new FactoryUpdateLog<>(currentFactoryRoot.createFactoryLogTree(), removed.stream().map(r->r.internal().createFactoryLogEntry()).collect(Collectors.toSet()),mergeDiff,totalUpdateDuration,null);
        }

    }

    private void determineRecreationNeedFromRoot(R previousFactoryCopyRoot) {
        currentFactoryRoot.determineRecreationNeedFromRoot(getChangedFactories(this.currentFactoryRoot, previousFactoryCopyRoot));
    }

    Set<FactoryBase<?,R>> getChangedFactories(RootFactoryWrapper<R> currentFactoryRoot, R previousFactoryCopyRoot){
        //one might think that the merger could do the change detection but that don't work for views and separation of concern is better anyway
        final HashSet<FactoryBase<?,R>> result = new HashSet<>();
        final Map<UUID, FactoryBase<?,R>> previousFactories = previousFactoryCopyRoot.internal().collectChildFactoryMap();
        for (FactoryBase<?,R> data: currentFactoryRoot.collectChildFactories()){
            final FactoryBase<?,R> previousFactory = previousFactories.get(data.getId());
            if (previousFactory!=null){
                this.collectChanged(data,previousFactory,result);
            }
        }
        return result;
    }
    public <FO extends FactoryBase<?,R>> void collectChanged(FO currentFactoryRoot, FO previousFactory, Set<FactoryBase<?,R>> changed){
        currentFactoryRoot.internal().visitAttributesForMatch(previousFactory, (name,currentAttribute, previousAttribute) -> {
            if (!currentAttribute.internal_match(previousAttribute)){
                changed.add(currentFactoryRoot);
                return false;
            }
            return true;
        });
    }


    public List<FactoryBase<?,R>> getRemovedFactories(Collection<FactoryBase<?,R>> previousFactories, Set<FactoryBase<?,R>> newFactories){
        final ArrayList<FactoryBase<?,R>> result = new ArrayList<>();
        previousFactories.forEach(previous -> {
            if (!newFactories.contains(previous)){
                result.add(previous);
            }
        });
        return result;
    }

    /** get the merge result  but don't execute the merge and liveObjects updates
     * @param commonVersion commonVersion
     * @param newVersion newVersion
     * @param permissionChecker permissionChecker
     * @return MergeDiffInfo*/
    public MergeDiffInfo<R> simulateUpdate(R commonVersion , R newVersion,  Function<String, Boolean> permissionChecker){
        newVersion.internal().loopDetector();

        DataMerger<R> dataMerger = new DataMerger<>(currentFactoryRoot.copy(), commonVersion, newVersion);
        return dataMerger.createMergeResult(permissionChecker).executeMerge();
    }


    public R getCurrentFactory(){
        if (currentFactoryRoot==null){
            return null;
        }
        return currentFactoryRoot.getRoot();
    }

    @SuppressWarnings("unchecked")
    public L start(RootFactoryWrapper<R> newFactory){
        FactoryBase<?,?> factoryBaseInFocus=null;//for better exception reporting
        try {
            currentFactoryRoot =newFactory;

            Collection<FactoryBase<?,R>> factoriesInCreateAndStartOrder = newFactory.getFactoriesInCreateAndStartOrder();
            for (FactoryBase<?,?> factory : factoriesInCreateAndStartOrder) {
                factoryBaseInFocus=factory;
                factory.internal().instance();
            }

            for (FactoryBase<?,?> factory : factoriesInCreateAndStartOrder) {
                factoryBaseInFocus=factory;
                factory.internal().start();
            }
            logger.info(currentFactoryRoot.logStartDisplayTextDeep());
            return currentFactoryRoot.getRoot().internal().getLiveObject();
        } catch (Exception e){
            factoryExceptionHandler.startException(e,factoryBaseInFocus,new ExceptionResponseAction(this,null,currentFactoryRoot,new ArrayList<>()));
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public void stop(){
        if (currentFactoryRoot==null){
            throw new IllegalStateException("server is not started");
        }
        Collection<FactoryBase<?,R>> factories = currentFactoryRoot.getFactoriesInDestroyOrder();
        FactoryBase<?,?> factoryBaseInFocus=null;//for better exception reporting
        try {
            for (FactoryBase<?,?> factory: factories){
                factoryBaseInFocus=factory;
                factory.internal().destroyRemoved();
            }
        } catch(Exception e){
            factoryExceptionHandler.destroyException(e,factoryBaseInFocus,new ExceptionResponseAction(this, null,null,new ArrayList<>()));
        }

    }

    public void resetAfterCrash() {
        for (FactoryBase<?,?> factory : currentFactoryRoot.getFactoriesInDestroyOrder()) {
            factory.internal().cleanUpAfterCrash();
        }
        this.currentFactoryRoot=null;
    }
}
