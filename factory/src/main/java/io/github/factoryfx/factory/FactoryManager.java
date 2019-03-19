package io.github.factoryfx.factory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Throwables;
import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.DataViewListReferenceAttribute;
import io.github.factoryfx.data.attribute.DataViewReferenceAttribute;
import io.github.factoryfx.data.merge.DataMerger;
import io.github.factoryfx.data.merge.MergeDiffInfo;
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

        Collection<FactoryBase<?,?>> previousFactories = currentFactoryRoot.getFactoriesInDestroyOrder();
        previousFactories.forEach((f) -> f.internalFactory().resetLog());
        R previousFactoryCopyRoot = currentFactoryRoot.copy();

        FactoryBase<?,?> factoryBaseInFocus=null;//for better exception reporting

        MergeDiffInfo<R> mergeDiff = currentFactoryRoot.merge(commonVersion, newVersion, permissionChecker);
        long totalUpdateDuration = 0;
        List<FactoryBase<?,?>> removed = new ArrayList<>();

        if (mergeDiff.successfullyMerged()) {
            try {
                final Collection<FactoryBase<?,?>> currentFactoriesAfterMerge = currentFactoryRoot.collectChildFactories();
                removed = getRemovedFactories(previousFactories, currentFactoriesAfterMerge);

                long start = System.nanoTime();
                determineRecreationNeedFromRoot(previousFactoryCopyRoot);

                //attention lifecycle method call order is important

                final Collection<FactoryBase<?,?>> factoriesInCreateAndStartOrder = currentFactoryRoot.getFactoriesInCreateAndStartOrder();
                for (FactoryBase<?,?> factory : factoriesInCreateAndStartOrder) {
                    factoryBaseInFocus=factory;
                    factory.internalFactory().instance();
                    //createWithExceptionHandling(factory, new RootFactoryWrapper<>(previousFactoryCopyRoot), currentFactoryRoot);
                }

                for (FactoryBase<?,?> factory : currentFactoriesAfterMerge) {
                    factoryBaseInFocus=factory;
                    factory.internalFactory().destroyUpdated();
                }
                for (FactoryBase<?,?> factory : removed) {
                    factoryBaseInFocus=factory;
                    factory.internalFactory().destroyRemoved();
                }


                //factoriesInCreateAndStartOrder.forEach(this::startWithExceptionHandling);
                for (FactoryBase<?,?> factory : factoriesInCreateAndStartOrder) {
                    factoryBaseInFocus=factory;
                    factory.internalFactory().start();
                }
                totalUpdateDuration = System.nanoTime() - start;
                logger.info(currentFactoryRoot.logUpdateDisplayTextDeep());
                return new FactoryUpdateLog<>(currentFactoryRoot.createFactoryLogTree(), removed.stream().map(r->r.internalFactory().createFactoryLogEntry()).collect(Collectors.toSet()),mergeDiff,totalUpdateDuration,null);
            } catch(Exception e){
                factoryExceptionHandler.updateException(e,factoryBaseInFocus,new ExceptionResponseAction(this, new RootFactoryWrapper(previousFactoryCopyRoot),currentFactoryRoot,removed));
                return new FactoryUpdateLog<>(Throwables.getStackTraceAsString(e));
            }
        } else {
            return new FactoryUpdateLog<>(currentFactoryRoot.createFactoryLogTree(), removed.stream().map(r->r.internalFactory().createFactoryLogEntry()).collect(Collectors.toSet()),mergeDiff,totalUpdateDuration,null);
        }

    }

    private void determineRecreationNeedFromRoot(R previousFactoryCopyRoot) {
        currentFactoryRoot.determineRecreationNeedFromRoot(getChangedFactories(this.currentFactoryRoot,previousFactoryCopyRoot));

//        for (Data data: currentFactoryRoot.collectChildFactories()){
//            data.internal().visitAttributesFlat((name, attribute) -> {
//                if (!(attribute instanceof FactoryViewReferenceAttribute) && !(currentAttribute instanceof DataViewListReferenceAttribute)){//Data views have no function no need to check
//                    if (!currentAttribute.internal_mergeMatch(previousAttribute)){
//                        result.add(data);
//                    }
//                }
//
//            });
//        }
    }

    Set<Data> getChangedFactories(RootFactoryWrapper<R> currentFactoryRoot, R previousFactoryCopyRoot){
        //one might think that the merger could do the change detection but that don't work for views and separation of concern is better anyway
        final HashSet<Data> result = new HashSet<>();
        final HashMap<String, FactoryBase<?,?>> previousFactories = previousFactoryCopyRoot.internalFactory().collectChildFactoriesDeepMapFromRoot();
        for (Data data: currentFactoryRoot.collectChildFactories()){
            final FactoryBase<?,?> previousFactory = previousFactories.get(data.getId());
            if (previousFactory!=null){
                data.internal().visitAttributesDualFlat(previousFactory, (name, currentAttribute, previousAttribute) -> {
                    if (!(currentAttribute instanceof DataViewReferenceAttribute) && !(currentAttribute instanceof DataViewListReferenceAttribute)){//Data views have no function no need to check
                        if (!currentAttribute.internal_mergeMatch(previousAttribute)){
                            result.add(data);
                        }
                    }

                });
            }
        }
        return result;
    }

    public List<FactoryBase<?,?>> getRemovedFactories(Collection<FactoryBase<?,?>> previousFactories, Collection<FactoryBase<?,?>> newFactories){
        final ArrayList<FactoryBase<?,?>> result = new ArrayList<>();
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
        newVersion.internalFactory().loopDetector();

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

            Collection<FactoryBase<?,?>> factoriesInCreateAndStartOrder = newFactory.getFactoriesInCreateAndStartOrder();
            for (FactoryBase<?,?> factory : factoriesInCreateAndStartOrder) {
                factoryBaseInFocus=factory;
                factory.internalFactory().instance();
            }

            for (FactoryBase<?,?> factory : factoriesInCreateAndStartOrder) {
                factoryBaseInFocus=factory;
                factory.internalFactory().start();
            }
            logger.info(currentFactoryRoot.logStartDisplayTextDeep());
            return currentFactoryRoot.getRoot().internalFactory().getLiveObject();
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
        Collection<FactoryBase<?,?>> factories = currentFactoryRoot.getFactoriesInDestroyOrder();
        FactoryBase<?,?> factoryBaseInFocus=null;//for better exception reporting
        try {
            for (FactoryBase<?,?> factory: factories){
                factoryBaseInFocus=factory;
                factory.internalFactory().destroyRemoved();
            }
        } catch(Exception e){
            factoryExceptionHandler.destroyException(e,factoryBaseInFocus,new ExceptionResponseAction(this, null,null,new ArrayList<>()));
        }

    }

    public void resetAfterCrash() {
        for (FactoryBase<?,?> factory : currentFactoryRoot.getFactoriesInDestroyOrder()) {
            factory.internalFactory().cleanUpAfterCrash();
        }
        this.currentFactoryRoot=null;
    }
}
