package io.github.factoryfx.factory;

import java.util.*;
import java.util.function.Function;

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
    private final FactoryExceptionHandler<L,R> factoryExceptionHandler;

    public FactoryManager(FactoryExceptionHandler<L,R> factoryExceptionHandler) {
        this.factoryExceptionHandler = factoryExceptionHandler;
        if (factoryExceptionHandler instanceof AllOrNothingFactoryExceptionHandler){
            logger.warn("only AllOrNothingFactoryExceptionHandler is set therefore no exception will be logged. Usually this setup is wrong and the handler should be wrapped with LoggingFactoryExceptionHandler");
        }
    }

    public FactoryUpdateLog<R> update(R commonVersion , R newVersion, Function<String,Boolean> permissionChecker){
        if (currentFactoryRoot==null) {
            throw new IllegalStateException("update on a not started manager");
        }


        return this.update((root, idToFactory) -> {
            Collection<FactoryBase<?,R>> previousFactories = currentFactoryRoot.getFactoriesInDestroyOrder();
            previousFactories.forEach((f) -> f.internal().resetLog());
            RootFactoryWrapper.MergeResult<R> merge = currentFactoryRoot.merge(commonVersion, newVersion, permissionChecker);
            return merge.mergeDiffInfo;
        });

    }

    private FactoryUpdateLog<R> updateCurrentFactory(MergeDiffInfo<R> mergeDiffInfo) {
        long totalUpdateDuration;
        FactoryBase<?,?> factoryBaseInFocus=null;//for better exception reporting
        try {
            long start = System.nanoTime();

            final Collection<FactoryBase<?,R>> currentFactoriesAfterMerge = currentFactoryRoot.collectChildFactories();
            Set<FactoryBase<?,?>> removed = currentFactoryRoot.getRemoved();//getRemovedFactories(previousFactories, new HashSet<>(currentFactoriesAfterMerge));


            currentFactoryRoot.determineRecreationNeedFromRoot(currentFactoryRoot.getRoot().internal().getModified());

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
            for (FactoryBase<?,?> factory : removed) {
                factoryBaseInFocus=factory;
                factory.internal().destroy();
            }


            //factoriesInCreateAndStartOrder.forEach(this::startWithExceptionHandling);
            for (FactoryBase<?,R> factory : factoriesInCreateAndStartOrder) {
                factoryBaseInFocus=factory;
                factory.internal().start();
            }
            totalUpdateDuration = System.nanoTime() - start;
            String log = currentFactoryRoot.logUpdateDisplayTextDeep();
            logger.info(log);
            return new FactoryUpdateLog<>(log, mergeDiffInfo,totalUpdateDuration,null);
        } catch(Exception e){
            factoryExceptionHandler.updateException(e,factoryBaseInFocus,new ExceptionResponseAction<>(this,currentFactoryRoot));
            return new FactoryUpdateLog<>(Throwables.getStackTraceAsString(e));
        }
    }

    List<FactoryBase<?,R>> getRemovedFactories(Collection<FactoryBase<?,R>> previousFactories, Set<FactoryBase<?,R>> newFactories){
        final ArrayList<FactoryBase<?,R>> result = new ArrayList<>(previousFactories);
        result.removeAll(newFactories);
        return result;
    }

    /** get the merge result  but don't execute the merge and liveObjects updates
     * @param commonVersion commonVersion
     * @param newVersion newVersion
     * @param permissionChecker permissionChecker
     * @return MergeDiffInfo*/
    public MergeDiffInfo<R> simulateUpdate(R commonVersion , R newVersion,  Function<String, Boolean> permissionChecker){
        newVersion.internal().finalise();
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
            factoryExceptionHandler.startException(e,factoryBaseInFocus,new ExceptionResponseAction<>(this,currentFactoryRoot));
            return null;
        }
    }

    public void stop(){
        if (!isStarted()){
            throw new IllegalStateException("server is not started");
        }
        Collection<FactoryBase<?,R>> factories = currentFactoryRoot.getFactoriesInDestroyOrder();
        FactoryBase<?,?> factoryBaseInFocus=null;//for better exception reporting
        try {
            for (FactoryBase<?,?> factory: factories){
                factoryBaseInFocus=factory;
                factory.internal().destroy();
            }
        } catch(Exception e){
            factoryExceptionHandler.destroyException(e,factoryBaseInFocus,new ExceptionResponseAction<>(this, null));
        }
        currentFactoryRoot=null;

    }

    public void resetAfterCrash() {
        for (FactoryBase<?,?> factory : currentFactoryRoot.getRoot().internal().getFactoriesInDestroyOrder()) {
            factory.internal().cleanUpAfterCrash();
        }
        for (FactoryBase<?, ?> factory : currentFactoryRoot.getRoot().internal().getRemoved()) {
            factory.internal().cleanUpAfterCrash();
        }
        for (FactoryBase<?, R> factory : currentFactoryRoot.getRoot().internal().collectChildrenDeep()) {
            factory.internal().resetModificationFlat();
        }
        for (FactoryBase<?, ?> factory : currentFactoryRoot.getRoot().internal().getRemoved()) {
            factory.internal().resetModificationFlat();
        }
        currentFactoryRoot.getRoot().internal().needReFinalisation();
        start(new RootFactoryWrapper<>(currentFactoryRoot.getRoot()));
    }

    public boolean isStarted(){
        return currentFactoryRoot!=null;
    }

    private FactoryUpdateLog<R> update(FactoryUpdateMerge<R> updater) {
        currentFactoryRoot.collectChildFactories();

        try {
            MergeDiffInfo<R> mergeDiffInfo = updater.update(currentFactoryRoot.getRoot(), currentFactoryRoot.getRoot().internal().collectChildFactoryMap());
            currentFactoryRoot.getRoot().internal().needReFinalisation();
            currentFactoryRoot=new RootFactoryWrapper<>(currentFactoryRoot.getRoot());
            return this.updateCurrentFactory(mergeDiffInfo);
        } catch (Exception e){
            logger.error("factory reset after exception during update",e);
            for (FactoryBase<?, R> factory : currentFactoryRoot.collectChildFactories()) {
                factory.internal().resetModificationFlat();
            }
            return new FactoryUpdateLog<>(Throwables.getStackTraceAsString(e));
        } finally {
            for (FactoryBase<?, R> factory : currentFactoryRoot.collectChildFactories()) {
                factory.internal().clearModifyStateFlat();
            }
        }
    }

    public FactoryUpdateLog<R> update(FactoryUpdate<R> updater) {
        return update((root, idToFactory) -> {
            updater.update(root,idToFactory);
            return null;
        });
    }
}
