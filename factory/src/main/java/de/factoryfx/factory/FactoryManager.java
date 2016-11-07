package de.factoryfx.factory;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.TreeTraverser;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.data.merge.MergeResultEntry;

public class FactoryManager<L,V,T extends FactoryBase<L,V>> {

    private T currentFactory;

    @SuppressWarnings("unchecked")
    public MergeDiff update(T commonVersion , T newVersion){
        newVersion.loopDetector();
        LinkedHashSet<FactoryBase<?,?>> previousLiveObjects = stopFactoryProvider.apply(currentFactory);

        DataMerger dataMerger = new DataMerger(currentFactory, commonVersion, newVersion);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        if (mergeDiff.hasNoConflicts()){
            for (FactoryBase<?,?> current : currentFactory.collectChildFactoriesDeep()){
                current.unMarkChanged();
            }
            for (MergeResultEntry mergeResultEntry: mergeDiff.getMergeInfos()){
                //TODO check cast required
                ((FactoryBase<?,?>)mergeResultEntry.parent).markChanged();
            }

            LinkedHashSet<FactoryBase<?,?>> changedFactories = startFactoryProvider.apply(currentFactory)
                    .stream().filter(factoryBase -> factoryBase.changedDeep()).collect(Collectors.toCollection(LinkedHashSet::new));

            currentFactory.instance();


            LinkedHashSet<FactoryBase<?,?>> newFactories = startFactoryProvider.apply(currentFactory);
            updateLiveObjects(previousLiveObjects,changedFactories,newFactories);
        }
        return mergeDiff;
    }


    /** get the merge result  but don't execute the merge and liveobjects Update*/
    @SuppressWarnings("unchecked")
    public MergeDiff simulateUpdate(T commonVersion , T newVersion){
        newVersion.loopDetector();

        DataMerger dataMerger = new DataMerger(currentFactory, commonVersion, newVersion);
        return dataMerger.createMergeResult();
    }

    private void updateLiveObjects(LinkedHashSet<FactoryBase<?,?>> previousFactories, LinkedHashSet<FactoryBase<?,?>> changedFactories , LinkedHashSet<FactoryBase<?,?>> newFactories){
        for (FactoryBase<?,?> previousLiveObject: previousFactories){
            if (!newFactories.contains(previousLiveObject)){
                previousLiveObject.destroy();
            }
        }

        for (FactoryBase<?,?> newLiveObject: newFactories){
            if (changedFactories.contains(newLiveObject)){
                newLiveObject.start();
                continue;
            }
            if (!previousFactories.contains(newLiveObject)){
                newLiveObject.start();
                continue;
            }
        }
    }

    TreeTraverser<FactoryBase<?,?>> factoryTraverser = new TreeTraverser<FactoryBase<?,?>>() {
        @Override
        public Iterable<FactoryBase<?,?>> children(FactoryBase<?,?> factory) {
            return factory.internal().collectChildrenFlat();
        }
    };
    private Function<T,LinkedHashSet<FactoryBase<?,?>>> startFactoryProvider = root -> {
        LinkedHashSet<FactoryBase<?,?>> result = new LinkedHashSet<>();
        for (FactoryBase<?,?> factory : factoryTraverser.postOrderTraversal(root)) {
            result.add(factory);
        }
        return result;
    };
    public void customizeStartOrder(Function<T,LinkedHashSet<FactoryBase<?,?>>> orderProvider){
        startFactoryProvider =orderProvider;
    }

    private Function<T,LinkedHashSet<FactoryBase<?,?>>> stopFactoryProvider = root -> {
        LinkedHashSet<FactoryBase<?,?>> result = new LinkedHashSet<>();
        for (FactoryBase<?,?> factory : factoryTraverser.breadthFirstTraversal(root)) {
            result.add(factory);
        }
        return result;
    };
    public void customizeStopOrder(Function<T,LinkedHashSet<FactoryBase<?,?>>> orderProvider){
        stopFactoryProvider =orderProvider;
    }

    public T getCurrentFactory(){
        return currentFactory;
    }

    @SuppressWarnings("unchecked")
    public void start(T newFactory){
        newFactory.loopDetector();
        currentFactory=newFactory;

        newFactory.instance();

        HashSet<FactoryBase<?,?>> newLiveObjects = startFactoryProvider.apply(newFactory);

        for (FactoryBase<?,?> newLiveObject: newLiveObjects){
            newLiveObject.start();
        }
    }

    @SuppressWarnings("unchecked")
    public void stop(){
        HashSet<FactoryBase<?,?>> liveObjects = stopFactoryProvider.apply(currentFactory);

        for (FactoryBase<?,?> newLiveObject: liveObjects){
            newLiveObject.destroy();
        }
    }

    @SuppressWarnings("unchecked")
    public V query(V visitor){
        for (FactoryBase<?,V> factory: currentFactory.collectChildFactoriesDeep()){
            factory.runtimeQuery(visitor);
        }
        return visitor;
    }

}
