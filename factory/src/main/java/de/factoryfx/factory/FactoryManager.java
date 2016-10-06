package de.factoryfx.factory;

import java.util.HashSet;
import java.util.Locale;
import java.util.function.Function;

import com.google.common.collect.TreeTraverser;
import de.factoryfx.data.merge.FactoryMerger;
import de.factoryfx.data.merge.MergeDiff;
import de.factoryfx.data.merge.MergeResultEntry;

public class FactoryManager<L,V,T extends FactoryBase<L,V>> {

    private T currentFactory;

    @SuppressWarnings("unchecked")
    public MergeDiff update(T commonVersion , T newVersion, Locale locale){
        newVersion.loopDetector();
        HashSet<FactoryBase<?,?>> previousLiveObjects = stopLiveObjectProvider.apply(currentFactory);

        FactoryMerger factoryMerger = new FactoryMerger(currentFactory, commonVersion, newVersion);
        factoryMerger.setLocale(locale);
        MergeDiff mergeDiff= factoryMerger.mergeIntoCurrent();
        if (mergeDiff.hasNoConflicts()){
            for (FactoryBase<?,?> current : currentFactory.collectChildFactoriesDeep()){
                current.unMarkChanged();
            }
            for (MergeResultEntry<?> mergeResultEntry: mergeDiff.getMergeInfos()){
                //TODO check cast required
                ((FactoryBase<?,?>)mergeResultEntry.parent).markChanged();
            }


            currentFactory.instance();


            HashSet<FactoryBase<?,?>> newLiveObjects = startLiveObjectProvider.apply(currentFactory);
            updateLiveObjects(previousLiveObjects,newLiveObjects);
        }
        return mergeDiff;
    }


    /** get the merge result  but don't execute the merge and liveobjects Update*/
    @SuppressWarnings("unchecked")
    public MergeDiff simulateUpdate(T commonVersion , T newVersion, Locale locale){
        newVersion.loopDetector();

        FactoryMerger factoryMerger = new FactoryMerger(currentFactory, commonVersion, newVersion);
        factoryMerger.setLocale(locale);
        return factoryMerger.createMergeResult();
    }

    public MergeDiff update(T commonVersion , T newVersion){
        return update(commonVersion , newVersion,Locale.ENGLISH);
    }


    private void updateLiveObjects(HashSet<FactoryBase<?,?>> previousLiveObjects, HashSet<FactoryBase<?,?>> newLiveObjects){
        for (FactoryBase<?,?> previousLiveObject: previousLiveObjects){
            if (!newLiveObjects.contains(previousLiveObject)){
                previousLiveObject.stop();
            }
        }

        for (FactoryBase<?,?> newLiveObject: newLiveObjects){
            if (previousLiveObjects.contains(newLiveObject)){
                //nothing reused live object
            }
            if (!previousLiveObjects.contains(newLiveObject)){
                newLiveObject.start();
            }
        }
    }

    TreeTraverser<FactoryBase<?,?>> factoryTraverser = new TreeTraverser<FactoryBase<?,?>>() {
        @Override
        public Iterable<FactoryBase<?,?>> children(FactoryBase<?,?> factory) {
            return factory.collectChildrenFlat();
        }
    };
    private Function<T,HashSet<FactoryBase<?,?>>> startLiveObjectProvider = root -> {
        HashSet<FactoryBase<?,?>> result = new HashSet<>();
        for (FactoryBase<?,?> factory : factoryTraverser.postOrderTraversal(root)) {
            result.add(factory);
        }
        return result;
    };
    public void customizeStartOrder(Function<T,HashSet<FactoryBase<?,?>>> orderProvider){
        startLiveObjectProvider =orderProvider;
    }

    private Function<T,HashSet<FactoryBase<?,?>>> stopLiveObjectProvider = root -> {
        HashSet<FactoryBase<?,?>> result = new HashSet<>();
        for (FactoryBase<?,?> factory : factoryTraverser.breadthFirstTraversal(root)) {
            result.add(factory);
        }
        return result;
    };
    public void customizeStopOrder(Function<T,HashSet<FactoryBase<?,?>>> orderProvider){
        stopLiveObjectProvider =orderProvider;
    }

    public T getCurrentFactory(){
        return currentFactory;
    }

    @SuppressWarnings("unchecked")
    public void start(T newFactory){
        newFactory.loopDetector();
        currentFactory=newFactory;

        newFactory.instance();

        HashSet<FactoryBase<?,?>> newLiveObjects = startLiveObjectProvider.apply(newFactory);

        for (FactoryBase<?,?> newLiveObject: newLiveObjects){
            newLiveObject.start();
        }
    }

    @SuppressWarnings("unchecked")
    public void stop(){
        HashSet<FactoryBase<?,?>> liveObjects = stopLiveObjectProvider.apply(currentFactory);

        for (FactoryBase<?,?> newLiveObject: liveObjects){
            newLiveObject.stop();
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
