package de.factoryfx.factory;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.TreeTraverser;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.data.merge.MergeResultEntry;
import de.factoryfx.factory.exception.ExceptionResponseAction;
import de.factoryfx.factory.exception.FactoryExceptionHandler;

public class FactoryManager<L,V,T extends FactoryBase<L,V>> {

    private T currentFactory;
    private final FactoryExceptionHandler<V> factoryExceptionHandler;

    public FactoryManager(FactoryExceptionHandler<V> factoryExceptionHandler) {
        this.factoryExceptionHandler = factoryExceptionHandler;
    }

    @SuppressWarnings("unchecked")
    public MergeDiff update(T commonVersion , T newVersion){
        newVersion.internalFactory().loopDetector();
        LinkedHashSet<FactoryBase<?,V>> previousFactories = getFactoriesInDestroyOrder(currentFactory);

        DataMerger dataMerger = new DataMerger(currentFactory, commonVersion, newVersion);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        if (mergeDiff.hasNoConflicts()){
            currentFactory.internalFactory().loopDetector();

            Set<FactoryBase<?,?>> changedFactories = new HashSet<>();
            for (MergeResultEntry mergeResultEntry: mergeDiff.getMergeInfos()){
                if (mergeResultEntry.parent instanceof FactoryBase) {
                    changedFactories.add((FactoryBase<?,?>)mergeResultEntry.parent);
                }
            }
            currentFactory.internalFactory().determineRecreationNeed(changedFactories);

            final LinkedHashSet<FactoryBase<?, V>> factoriesInCreateAndStartOrder = getFactoriesInCreateAndStartOrder(currentFactory);
            factoriesInCreateAndStartOrder.forEach(factoryBase -> createWithExceptionHandling(factoryBase));

            destroyFactories(previousFactories,currentFactory.internalFactory().collectChildFactoriesDeep());

            factoriesInCreateAndStartOrder.forEach(factoryBase -> startWithExceptionHandling(factoryBase));
        }
        return mergeDiff;
    }

    /** get the merge result  but don't execute the merge and liveobjects update*/
    @SuppressWarnings("unchecked")
    public MergeDiff simulateUpdate(T commonVersion , T newVersion){
        newVersion.internalFactory().loopDetector();

        DataMerger dataMerger = new DataMerger(currentFactory, commonVersion, newVersion);
        return dataMerger.createMergeResult();
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
        return currentFactory;
    }

    @SuppressWarnings("unchecked")
    public void start(T newFactory){
        newFactory.internalFactory().loopDetector();
        currentFactory=newFactory;

        HashSet<FactoryBase<?,V>> factoriesInCreateAndStartOrder = getFactoriesInCreateAndStartOrder(newFactory);
        factoriesInCreateAndStartOrder.forEach(factoryBase -> createWithExceptionHandling(factoryBase));
        factoriesInCreateAndStartOrder.forEach(factoryBase -> startWithExceptionHandling(factoryBase));
    }

    @SuppressWarnings("unchecked")
    public void stop(){
        HashSet<FactoryBase<?,V>> factories = getFactoriesInDestroyOrder(currentFactory);

        for (FactoryBase<?,V> factory: factories){
            destroyWithExceptionHandling(factory,new HashSet<>());
        }
    }

    @SuppressWarnings("unchecked")
    public V query(V visitor){
        for (FactoryBase<?,V> factory: currentFactory.internalFactory().collectChildFactoriesDeep()){
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
