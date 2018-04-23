package de.factoryfx.factory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.DataViewListReferenceAttribute;
import de.factoryfx.data.attribute.DataViewReferenceAttribute;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.ExceptionResponseAction;
import de.factoryfx.factory.exception.FactoryExceptionHandler;
import de.factoryfx.factory.log.FactoryUpdateLog;
import org.slf4j.LoggerFactory;

/**
 * Manage application lifecycle (start,stop,update)
 *
 * @param <V> Visitor
 * @param <R> Root
 */
public class FactoryManager<V,R extends FactoryBase<?,V,R>> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FactoryManager.class);

    private R currentFactoryRoot;
    private final FactoryExceptionHandler factoryExceptionHandler;


    public FactoryManager(FactoryExceptionHandler factoryExceptionHandler) {
        this.factoryExceptionHandler = factoryExceptionHandler;
        if (factoryExceptionHandler instanceof AllOrNothingFactoryExceptionHandler){
            logger.warn("only AllOrNothingFactoryExceptionHandler is set therefore no exception will be logged. Usually this setup is wrong and the handler should be wrapped with LoggingFactoryExceptionHandler");
        }
    }

    @SuppressWarnings("unchecked")
    public FactoryUpdateLog<R> update(R commonVersion , R newVersion, Function<String,Boolean> permissionChecker){
        LinkedHashSet<FactoryBase<?,V,R>> previousFactories = getFactoriesInDestroyOrder(currentFactoryRoot);
        previousFactories.forEach((f)->f.internalFactory().resetLog());

        R previousFactoryCopyRoot = currentFactoryRoot.internal().copyFromRoot();

        DataMerger<R> dataMerger = new DataMerger<>(currentFactoryRoot, commonVersion, newVersion);
        MergeDiffInfo<R> mergeDiff= dataMerger.mergeIntoCurrent(permissionChecker);
        long totalUpdateDuration=0;
        List<FactoryBase<?,V,R>> removed = new ArrayList<>();
        if (mergeDiff.successfullyMerged()){
            final Set<FactoryBase<?, V,R>> newFactories = currentFactoryRoot.internalFactory().collectChildFactoriesDeepFromRoot();

            long start=System.nanoTime();
            currentFactoryRoot.internalFactory().determineRecreationNeedFromRoot(getChangedFactories(previousFactoryCopyRoot));

            final LinkedHashSet<FactoryBase<?, V, R>> factoriesInCreateAndStartOrder = getFactoriesInCreateAndStartOrder(currentFactoryRoot);
            factoriesInCreateAndStartOrder.forEach(this::createWithExceptionHandling);

            removed=getRemovedFactories(previousFactories,newFactories);

            newFactories.forEach(this::destroyUpdatedWithExceptionHandling);
            removed.forEach(this::destroyRemovedWithExceptionHandling);//TODO is the order correct, do we care?

            factoriesInCreateAndStartOrder.forEach(this::startWithExceptionHandling);
            totalUpdateDuration=System.nanoTime()-start;

        }

        return new FactoryUpdateLog<>(currentFactoryRoot.internalFactory().createFactoryLogEntry(), removed.stream().map(r->r.internalFactory().createFactoryLogEntryFlat()).collect(Collectors.toSet()),mergeDiff,totalUpdateDuration);
    }

    private Set<Data> getChangedFactories(R previousFactoryCopyRoot){
        //one might think that the merger could do the change detection but that don't work for views and separation of concern is better anyway
        final HashSet<Data> result = new HashSet<>();
        final HashMap<String, FactoryBase<?, V,R>> previousFactories = previousFactoryCopyRoot.internalFactory().collectChildFactoriesDeepMapFromRoot();
        for (Data data: currentFactoryRoot.internalFactory().collectChildFactoriesDeepFromRoot()){
            final FactoryBase<?, V,R> previousFactory = previousFactories.get(data.getId());
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

    public List<FactoryBase<?,V,R>> getRemovedFactories(Set<FactoryBase<?,V,R>> previousFactories, Set<FactoryBase<?,V,R>> newFactories){
        final ArrayList<FactoryBase<?, V,R>> result = new ArrayList<>();
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
    @SuppressWarnings("unchecked")
    public MergeDiffInfo<R> simulateUpdate(R commonVersion , R newVersion,  Function<String, Boolean> permissionChecker){
        newVersion.internalFactory().loopDetector();

        DataMerger<R> dataMerger = new DataMerger<>(currentFactoryRoot.internal().copyFromRoot(), commonVersion, newVersion);
        return dataMerger.createMergeResult(permissionChecker).executeMerge();
    }




    private LinkedHashSet<FactoryBase<?,V,R>> getFactoriesInCreateAndStartOrder(R root){
        LinkedHashSet<FactoryBase<?,V,R>> result = new LinkedHashSet<>();
        for (FactoryBase<?,V,R> factory : root.internalFactory().postOrderTraversalFromRoot()) {
            result.add(factory);
        }
        return result;
    }
    private LinkedHashSet<FactoryBase<?,V,R>> getFactoriesInDestroyOrder(R root){
        LinkedHashSet<FactoryBase<?,V,R>> result = new LinkedHashSet<>();
        for (FactoryBase<?,V,R> factory : root.internalFactory().breadthFirstTraversalFromRoot()) {
            result.add(factory);
        }
        return result;
    }

    public R getCurrentFactory(){
        return currentFactoryRoot;
    }

    @SuppressWarnings("unchecked")
    public void start(R newFactory){
        newFactory.internalFactory().loopDetector();
        currentFactoryRoot =newFactory;

        HashSet<FactoryBase<?,V,R>> factoriesInCreateAndStartOrder = getFactoriesInCreateAndStartOrder(newFactory);
        factoriesInCreateAndStartOrder.forEach(this::createWithExceptionHandling);
        factoriesInCreateAndStartOrder.forEach(this::startWithExceptionHandling);

        logger.info(currentFactoryRoot.internalFactory().createFactoryLogEntry().toStringFromRoot());
    }

    @SuppressWarnings("unchecked")
    public void stop(){
        HashSet<FactoryBase<?,V,R>> factories = getFactoriesInDestroyOrder(currentFactoryRoot);

        for (FactoryBase<?,V,R> factory: factories){
            destroyRemovedWithExceptionHandling(factory);
        }
    }

    @SuppressWarnings("unchecked")
    public V query(V visitor){
        for (FactoryBase<?,V,R> factory: currentFactoryRoot.internalFactory().collectChildFactoriesDeepFromRoot()){
            factory.internalFactory().runtimeQuery(visitor);
        }
        return visitor;
    }

    private void createWithExceptionHandling(FactoryBase<?,V,R> factory){
        try {
            factory.internalFactory().instance();
        } catch (Exception e){
            factoryExceptionHandler.createOrRecreateException(e,factory,new ExceptionResponseAction(this));
        }
    }

    private void startWithExceptionHandling(FactoryBase<?,V,R> factory){
        try {
            factory.internalFactory().start();
        } catch (Exception e){
            factoryExceptionHandler.startException(e,factory,new ExceptionResponseAction(this));
        }
    }

    private void destroyUpdatedWithExceptionHandling(FactoryBase<?,V,R> factory){
        try {
            //TODO destroy logging seems wrong cause this is called for all factories and method impl checks if destroy needed
            factory.internalFactory().destroyUpdated();
        } catch (Exception e){
            factoryExceptionHandler.destroyException(e,factory,new ExceptionResponseAction(this));
        }
    }

    private void destroyRemovedWithExceptionHandling(FactoryBase<?,V,R> factory){
        try {
            factory.internalFactory().destroyRemoved();
        } catch (Exception e){
            factoryExceptionHandler.destroyException(e,factory,new ExceptionResponseAction(this));
        }
    }

}
