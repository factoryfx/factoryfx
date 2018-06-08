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
        Collection<FactoryBase<?,?,?>> previousFactories = currentFactoryRoot.getFactoriesInDestroyOrder();
        previousFactories.forEach((f)->f.internalFactory().resetLog());

        R previousFactoryCopyRoot = currentFactoryRoot.copy();

        MergeDiffInfo<R> mergeDiff = currentFactoryRoot.merge(commonVersion,newVersion,permissionChecker);

        long totalUpdateDuration=0;
        List<FactoryBase<?,?,?>> removed = new ArrayList<>();
        if (mergeDiff.successfullyMerged()){
            final Collection<FactoryBase<?,?,?>> newFactories = currentFactoryRoot.collectChildFactories();

            long start=System.nanoTime();
            currentFactoryRoot.determineRecreationNeedFromRoot(getChangedFactories(previousFactoryCopyRoot));

            final Collection<FactoryBase<?,?,?>> factoriesInCreateAndStartOrder = currentFactoryRoot.getFactoriesInCreateAndStartOrder();
            factoriesInCreateAndStartOrder.forEach(this::createWithExceptionHandling);

            removed=getRemovedFactories(previousFactories,newFactories);

            newFactories.forEach(this::destroyUpdatedWithExceptionHandling);
            removed.forEach(this::destroyRemovedWithExceptionHandling);//TODO is the order correct, do we care?

            factoriesInCreateAndStartOrder.forEach(this::startWithExceptionHandling);
            totalUpdateDuration=System.nanoTime()-start;

        }

        return new FactoryUpdateLog<>(currentFactoryRoot.createFactoryLogTree(), removed.stream().map(r->r.internalFactory().createFactoryLogEntry()).collect(Collectors.toSet()),mergeDiff,totalUpdateDuration);
    }

    private Set<Data> getChangedFactories(R previousFactoryCopyRoot){
        //one might think that the merger could do the change detection but that don't work for views and separation of concern is better anyway
        final HashSet<Data> result = new HashSet<>();
        final HashMap<String, FactoryBase<?, ?,?>> previousFactories = previousFactoryCopyRoot.internalFactory().collectChildFactoriesDeepMapFromRoot();
        for (Data data: currentFactoryRoot.collectChildFactories()){
            final FactoryBase<?, ?,?> previousFactory = previousFactories.get(data.getId());
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

    public List<FactoryBase<?,?,?>> getRemovedFactories(Collection<FactoryBase<?,?,?>> previousFactories, Collection<FactoryBase<?,?,?>> newFactories){
        final ArrayList<FactoryBase<?,?,?>> result = new ArrayList<>();
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

        DataMerger<R> dataMerger = new DataMerger<>(currentFactoryRoot.copy(), commonVersion, newVersion);
        return dataMerger.createMergeResult(permissionChecker).executeMerge();
    }


    public R getCurrentFactory(){
        return currentFactoryRoot.getRoot();
    }

    @SuppressWarnings("unchecked")
    public void start(RootFactoryWrapper<R> newFactory){
        currentFactoryRoot =newFactory;

        Collection<FactoryBase<?,?,?>> factoriesInCreateAndStartOrder = newFactory.getFactoriesInCreateAndStartOrder();
        factoriesInCreateAndStartOrder.forEach(this::createWithExceptionHandling);
        factoriesInCreateAndStartOrder.forEach(this::startWithExceptionHandling);

        logger.info(currentFactoryRoot.logDisplayTextDeep());
    }

    @SuppressWarnings("unchecked")
    public void stop(){
        Collection<FactoryBase<?,?,?>> factories = currentFactoryRoot.getFactoriesInDestroyOrder();

        for (FactoryBase<?,?,?> factory: factories){
            destroyRemovedWithExceptionHandling(factory);
        }
    }

    @SuppressWarnings("unchecked")
    public V query(V visitor){
        for (FactoryBase<?,?,?> factory: currentFactoryRoot.collectChildFactories()){
            ((FactoryBase<?,V,?>)factory).internalFactory().runtimeQuery(visitor);
        }
        return visitor;
    }

    private void createWithExceptionHandling(FactoryBase<?,?,?> factory){
        try {
            factory.internalFactory().instance();
        } catch (Exception e){
            factoryExceptionHandler.createOrRecreateException(e,factory,new ExceptionResponseAction(this));
        }
    }

    private void startWithExceptionHandling(FactoryBase<?,?,?> factory){
        try {
            factory.internalFactory().start();
        } catch (Exception e){
            factoryExceptionHandler.startException(e,factory,new ExceptionResponseAction(this));
        }
    }

    private void destroyUpdatedWithExceptionHandling(FactoryBase<?,?,?> factory){
        try {
            //TODO destroy logging seems wrong cause this is called for all factories and method impl checks if destroy needed
            factory.internalFactory().destroyUpdated();
        } catch (Exception e){
            factoryExceptionHandler.destroyException(e,factory,new ExceptionResponseAction(this));
        }
    }

    private void destroyRemovedWithExceptionHandling(FactoryBase<?,?,?> factory){
        try {
            factory.internalFactory().destroyRemoved();
        } catch (Exception e){
            factoryExceptionHandler.destroyException(e,factory,new ExceptionResponseAction(this));
        }
    }

}
