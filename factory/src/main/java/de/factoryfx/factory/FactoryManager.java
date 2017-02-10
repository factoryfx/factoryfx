package de.factoryfx.factory;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.TreeTraverser;
import de.factoryfx.data.Data;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.data.merge.MergeResultEntry;

public class FactoryManager<L,V,T extends FactoryBase<L,V>> {

    private T currentFactory;

    @SuppressWarnings("unchecked")
    public MergeDiff update(T commonVersion , T newVersion){
        newVersion.internalFactory().loopDetector();
        LinkedHashSet<FactoryBase<?,?>> previousLiveObjects = destroyFactoryProvider.apply(currentFactory);

        DataMerger dataMerger = new DataMerger(currentFactory, commonVersion, newVersion);
        MergeDiff mergeDiff= dataMerger.mergeIntoCurrent();
        if (mergeDiff.hasNoConflicts()){
            for (FactoryBase<?,?> current : currentFactory.internalFactory().collectChildFactoriesDeep()){
                current.internalFactory().unMarkChanged();
            }
            for (MergeResultEntry mergeResultEntry: mergeDiff.getMergeInfos()){
                //TODO check cast required
                ((FactoryBase<?,?>)mergeResultEntry.parent).internalFactory().markChanged();
            }

            currentFactory.internalFactory().loopDetector();
            currentFactory.internalFactory().instance();

            startFactoryProvider.apply(currentFactory).forEach(factoryBase -> factoryBase.internalFactory().start());

            LinkedHashSet<FactoryBase<?,?>> newFactories = startFactoryProvider.apply(currentFactory);
            cleanupRemovedFactories(previousLiveObjects,newFactories);
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

    private void cleanupRemovedFactories(LinkedHashSet<FactoryBase<?,?>> previousFactories, LinkedHashSet<FactoryBase<?,?>> newFactories){
        for (FactoryBase<?,?> previousLiveObject: previousFactories){
            if (!newFactories.contains(previousLiveObject)){
                previousLiveObject.internalFactory().destroy();
            }
        }
    }

    @SuppressWarnings("unchecked")
    TreeTraverser<FactoryBase<?,?>> factoryTraverser = new TreeTraverser<FactoryBase<?,?>>() {
        @Override
        public Iterable<FactoryBase<?,?>> children(FactoryBase<?,?> factory) {
            Set<? extends Data> children = factory.internal().collectChildrenFlat();
            children.removeIf(c->!(c instanceof FactoryBase));
            return (Set<FactoryBase<?,?>>)children;
        }
    };
    private Function<T,LinkedHashSet<FactoryBase<?,?>>> startFactoryProvider = root -> {
        LinkedHashSet<FactoryBase<?,?>> result = new LinkedHashSet<>();
        for (FactoryBase<?,?> factory : factoryTraverser.postOrderTraversal(root)) {
            result.add(factory);
        }
        return result;
    };
    private Function<T,LinkedHashSet<FactoryBase<?,?>>> destroyFactoryProvider = root -> {
        LinkedHashSet<FactoryBase<?,?>> result = new LinkedHashSet<>();
        for (FactoryBase<?,?> factory : factoryTraverser.breadthFirstTraversal(root)) {
            result.add(factory);
        }
        return result;
    };

    public T getCurrentFactory(){
        return currentFactory;
    }

    @SuppressWarnings("unchecked")
    public void start(T newFactory){
        newFactory.internalFactory().loopDetector();
        currentFactory=newFactory;

        newFactory.internalFactory().instance();

        HashSet<FactoryBase<?,?>> newLiveObjects = startFactoryProvider.apply(newFactory);

        for (FactoryBase<?,?> newLiveObject: newLiveObjects){
            newLiveObject.internalFactory().start();
        }
    }

    @SuppressWarnings("unchecked")
    public void stop(){
        HashSet<FactoryBase<?,?>> liveObjects = destroyFactoryProvider.apply(currentFactory);

        for (FactoryBase<?,?> newLiveObject: liveObjects){
            newLiveObject.internalFactory().destroy();
        }
    }

    @SuppressWarnings("unchecked")
    public V query(V visitor){
        for (FactoryBase<?,V> factory: currentFactory.internalFactory().collectChildFactoriesDeep()){
            factory.internalFactory().runtimeQuery(visitor);
        }
        return visitor;
    }

}
