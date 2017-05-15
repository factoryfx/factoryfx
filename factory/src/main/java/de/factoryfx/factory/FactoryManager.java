package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.TreeTraverser;
import com.sun.net.httpserver.HttpServer;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.exception.AllOrNothingFactoryExceptionHandler;
import de.factoryfx.factory.exception.ExceptionResponseAction;
import de.factoryfx.factory.exception.FactoryExceptionHandler;
import de.factoryfx.factory.log.FactoryUpdateLog;
import org.slf4j.LoggerFactory;

public class FactoryManager<L,V,T extends FactoryBase<L,V>> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FactoryManager.class);

    private T currentFactoryRoot;
    private final FactoryExceptionHandler<V> factoryExceptionHandler;


    public FactoryManager(FactoryExceptionHandler<V> factoryExceptionHandler) {
        this.factoryExceptionHandler = factoryExceptionHandler;
        if (factoryExceptionHandler instanceof AllOrNothingFactoryExceptionHandler){
            logger.warn("only AllOrNothingFactoryExceptionHandler is set therefore no exception will be logged. Usually this setup is wrong and the handler should be wrapped with LoggingFactoryExceptionHandler");
        }
    }

    @SuppressWarnings("unchecked")
    public FactoryUpdateLog update(T commonVersion , T newVersion, Function<String,Boolean> permissionChecker){
        newVersion.internalFactory().loopDetector();
        LinkedHashSet<FactoryBase<?,V>> previousFactories = getFactoriesInDestroyOrder(currentFactoryRoot);
        previousFactories.forEach((f)->f.internalFactory().resetLog());

        T previousFactoryCopyRoot = currentFactoryRoot.internal().copyRoot();

        DataMerger dataMerger = new DataMerger(currentFactoryRoot, commonVersion, newVersion);
        MergeDiffInfo mergeDiff= dataMerger.mergeIntoCurrent(permissionChecker);
        long totalUpdateDuration=0;
        List<FactoryBase<?,V>> removed = new ArrayList<>();
        if (mergeDiff.successfullyMerged()){
            currentFactoryRoot.internalFactory().loopDetector();
            final Set<FactoryBase<?, V>> newFactories = currentFactoryRoot.internalFactory().collectChildFactoriesDeep();

            long start=System.nanoTime();
            currentFactoryRoot.internalFactory().determineRecreationNeed(getChangedFactories(previousFactoryCopyRoot));

            final LinkedHashSet<FactoryBase<?, V>> factoriesInCreateAndStartOrder = getFactoriesInCreateAndStartOrder(currentFactoryRoot);
            factoriesInCreateAndStartOrder.forEach(this::createWithExceptionHandling);

            destroyFactories(previousFactories, newFactories);

            factoriesInCreateAndStartOrder.forEach(this::startWithExceptionHandling);
            totalUpdateDuration=System.nanoTime()-start;
            removed=getRemovedFactories(previousFactories,newFactories);
        }

        return new FactoryUpdateLog(currentFactoryRoot.internalFactory().createFactoryLogEntry(), removed.stream().map(r->r.internalFactory().createFactoryLogEntryFlat()).collect(Collectors.toSet()),mergeDiff,totalUpdateDuration);
    }

    private Set<Data> getChangedFactories(T previousFactoryCopyRoot){
        //one might think that the merger could do the change detection but that don't work for views and separation of concern is better anyway
        final HashSet<Data> result = new HashSet<>();
        final HashMap<String, FactoryBase<?, V>> previousFactories = previousFactoryCopyRoot.internalFactory().collectChildFactoriesDeepMap();
        for (Data data: currentFactoryRoot.internalFactory().collectChildFactoriesDeep()){
            final FactoryBase<?, V> previousFactory = previousFactories.get(data.getId());
            if (previousFactory!=null){
                data.internal().visitAttributesDualFlat(previousFactory, (currentAttribute, previousAttribute) -> {
                    if (!currentAttribute.internal_match(previousAttribute)){
                        result.add(data);
                    }
                });
            }
        }
        return result;
    }

    public List<FactoryBase<?,V>> getRemovedFactories(Set<FactoryBase<?,V>> previousFactories, Set<FactoryBase<?,V>> newFactories){
        final ArrayList<FactoryBase<?, V>> result = new ArrayList<>();
        previousFactories.forEach(previous -> {
            if (!newFactories.contains(previous)){
                result.add(previous);
            }
        });
        return result;
    }

    /** get the merge result  but don't execute the merge and liveObjects updates*/
    @SuppressWarnings("unchecked")
    public MergeDiffInfo simulateUpdate(T commonVersion , T newVersion,  Function<String, Boolean> permissionChecker){
        newVersion.internalFactory().loopDetector();

        DataMerger dataMerger = new DataMerger(currentFactoryRoot, commonVersion, newVersion);
        return dataMerger.createMergeResult(permissionChecker);
    }

    private void destroyFactories(LinkedHashSet<FactoryBase<?,V>> previousFactories, Set<FactoryBase<?,V>> newFactories){
        for (FactoryBase<?,V> newFactory: newFactories){
            destroyWithExceptionHandling(newFactory,previousFactories);
        }
    }

    private final TreeTraverser<FactoryBase<?,V>> factoryTraverser = new TreeTraverser<FactoryBase<?,V>>() {
        @Override
        public Iterable<FactoryBase<?,V>> children(FactoryBase<?,V> factory) {
            return factory.internalFactory().collectChildrenFactoriesFlat();
        }
    };
    private LinkedHashSet<FactoryBase<?,V>> getFactoriesInCreateAndStartOrder(T root){
        LinkedHashSet<FactoryBase<?,V>> result = new LinkedHashSet<>();
        for (FactoryBase<?,V> factory : factoryTraverser.postOrderTraversal(root)) {
            result.add(factory);
        }
        return result;
    }
    private LinkedHashSet<FactoryBase<?,V>> getFactoriesInDestroyOrder(T root){
        LinkedHashSet<FactoryBase<?,V>> result = new LinkedHashSet<>();
        for (FactoryBase<?,V> factory : factoryTraverser.breadthFirstTraversal(root)) {
            result.add(factory);
        }
        return result;
    }

    public T getCurrentFactory(){
        return currentFactoryRoot;
    }

    @SuppressWarnings("unchecked")
    public void start(T newFactory){
        newFactory.internalFactory().loopDetector();
        currentFactoryRoot =newFactory;

        HashSet<FactoryBase<?,V>> factoriesInCreateAndStartOrder = getFactoriesInCreateAndStartOrder(newFactory);
        factoriesInCreateAndStartOrder.forEach(this::createWithExceptionHandling);
        factoriesInCreateAndStartOrder.forEach(this::startWithExceptionHandling);

//        FactoryLog factoryLog = new FactoryLog(currentFactoryRoot.internalFactory().createFactoryLogEntry(),null);
//        System.out.println(ObjectMapperBuilder.build().writeValueAsString(factoryLog));
    }

    @SuppressWarnings("unchecked")
    public void stop(){
        HashSet<FactoryBase<?,V>> factories = getFactoriesInDestroyOrder(currentFactoryRoot);

        for (FactoryBase<?,V> factory: factories){
            destroyWithExceptionHandling(factory,new HashSet<>());
        }
    }

    @SuppressWarnings("unchecked")
    public V query(V visitor){
        for (FactoryBase<?,V> factory: currentFactoryRoot.internalFactory().collectChildFactoriesDeep()){
            factory.internalFactory().runtimeQuery(visitor);
        }
        return visitor;
    }

    private void createWithExceptionHandling(FactoryBase<?,V> factory){
        try {
            factory.internalFactory().instance();
        } catch (Exception e){
            factoryExceptionHandler.createOrRecreateException(e,factory,new ExceptionResponseAction(this));
        }
    }

    private void startWithExceptionHandling(FactoryBase<?,V> factory){
        try {
            factory.internalFactory().start();
        } catch (Exception e){
            factoryExceptionHandler.startException(e,factory,new ExceptionResponseAction(this));
        }
    }

    private void destroyWithExceptionHandling(FactoryBase<?,V> factory, Set<FactoryBase<?,V>> previousFactories){
        try {
            factory.internalFactory().destroy(previousFactories);
        } catch (Exception e){
            factoryExceptionHandler.destroyException(e,factory,new ExceptionResponseAction(this));
        }
    }


}
