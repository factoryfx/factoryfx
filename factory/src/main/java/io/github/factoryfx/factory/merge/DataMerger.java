package io.github.factoryfx.factory.merge;

import io.github.factoryfx.factory.FactoryBase;

import java.util.*;
import java.util.function.Function;

public final class DataMerger<R extends FactoryBase<?,R>> {//final cause thread created in constructor, spotbugs: SC_START_IN_CTOR

    private final R currentData;
    private final R commonData;
    private final R newData;

//    private Map<UUID, FactoryBase<?,R>> currentMap;
//    private Map<UUID, FactoryBase<?,R>> commonMap;
//    private Map<UUID, FactoryBase<?,R>> newMap;
    
    List<Triple> mergeable=new ArrayList<>();
    private static class Triple<R extends FactoryBase<?,R>>{
        private final FactoryBase<?,R> currentFactory;
        private final FactoryBase<?,R> commonFactory;
        private final FactoryBase<?,R> newFactory;

        private Triple(FactoryBase<?, R> currentFactory, FactoryBase<?, R> commonFactory, FactoryBase<?, R> newFactory) {
            this.currentFactory = currentFactory;
            this.commonFactory = commonFactory;
            this.newFactory = newFactory;
        }
    }

    public DataMerger(R currentData, R commonData, R newData) {
        if (currentData==commonData){
            throw new IllegalArgumentException("Arguments: currentData and commonData can't be the same, use .utility().copy() to create a copy");
        }

        this.currentData = currentData;
        this.commonData = commonData;
        this.newData = newData;

        currentData.internal().finalise();
        commonData.internal().finalise();
        newData.internal().finalise();

        List<FactoryBase<?, R>> currentDataList=currentData.internal().collectChildrenDeep();
        List<FactoryBase<?, R>> commonDataList=commonData.internal().collectChildrenDeep();
        List<FactoryBase<?, R>> newDataDataList=newData.internal().collectChildrenDeep();

//         ExecutorService exec = Executors.newFixedThreadPool(3);
//        Future<List<FactoryBase<?,R>>> future1 = exec.submit(() -> currentData.internal().collectChildrenDeep());
//        Future<List<FactoryBase<?,R>>> future2 = exec.submit(() -> commonData.internal().collectChildrenDeep());
//        Future<List<FactoryBase<?,R>>> future3 = exec.submit(() -> newData.internal().collectChildrenDeep());
//        try {
//            currentDataList = future1.get();
//            commonDataList=future2.get();
//            newDataDataList=future3.get();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        exec.shutdown();


        //Performance optimisation for the case that the factory tree structure are identically
        //First use normal iteration and if not identically use map


        int matchEnd=-1;
        for (int i = 0; i < currentDataList.size(); i++) {
            if (i>=commonDataList.size() || i>=newDataDataList.size()){
                matchEnd=i;
                break;
            }
            FactoryBase<?, R> currentDataItem = currentDataList.get(i);
            FactoryBase<?, R> commonDataItem = commonDataList.get(i);
            FactoryBase<?, R> newDataDataItem = newDataDataList.get(i);
            if (currentDataItem.getId().equals(commonDataItem.getId()) &&
                currentDataItem.getId().equals(newDataDataItem.getId())
            ){
                mergeable.add(new Triple<>(currentDataItem,commonDataItem,newDataDataItem));

            } else {
                matchEnd=i;
                break;
            }
        }
        Map<UUID, FactoryBase<?,R>> currentDataMap = new HashMap<>();
        Map<UUID, FactoryBase<?,R>> commonDataMap = new HashMap<>();
        Map<UUID, FactoryBase<?,R>> newDataDataMap = new HashMap<>();

        if (matchEnd>=0){
            for (int i = matchEnd; i < currentDataList.size(); i++) {
                FactoryBase<?, R> item = currentDataList.get(i);
                currentDataMap.put(item.getId(),item);
            }
            for (int i = matchEnd; i < commonDataList.size(); i++) {
                FactoryBase<?, R> item = commonDataList.get(i);
                commonDataMap.put(item.getId(),item);
            }
            for (int i = matchEnd; i < newDataDataList.size(); i++) {
                FactoryBase<?, R> item = newDataDataList.get(i);
                newDataDataMap.put(item.getId(),item);
            }

            for (FactoryBase<?,R> item: currentDataMap.values()){
                if (item==currentData){//root compare even with wrong ids
                    mergeable.add(new Triple<>(item,commonData,newData));
                } else {
                    mergeable.add(new Triple<>(item,commonDataMap.get(item.getId()),newDataDataMap.get(item.getId())));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public MergeResult<R> createMergeResult(Function<String,Boolean> permissionChecker) {
        MergeResult<R> mergeResult = new MergeResult<>(currentData);

        for (Triple<R> entry : mergeable) {
            FactoryBase<?,R> originalValue = entry.commonFactory;
            FactoryBase<?,R> newValue = entry.newFactory;

            if (newValue==null && originalValue!=null){
                //check for conflict for removed object
                entry.currentFactory.internal().visitAttributesForMatch(originalValue, (name,currentAttribute, originalAttribute) -> {
                    if (!currentAttribute.internal_match(originalAttribute)){
                        mergeResult.addConflictInfo(new AttributeDiffInfo(name,entry.currentFactory.getId()));
                    }
                    return true;
                });
            }

            if (originalValue!=null && newValue!=null){
                FactoryBase value = entry.currentFactory;
                value.internal().merge(originalValue, newValue, mergeResult, permissionChecker);
            }
        }
        return mergeResult;
    }
//
//    private FactoryBase<?,R> getNewValue(Map.Entry<UUID, FactoryBase<?,R>> currentEntry) {
//        if (currentEntry.getValue()==currentData){//for root different id don't make sense
//            return newData;
//        }
//        return newMap.get(currentEntry.getKey());
//    }
//
//    private FactoryBase<?,R> getOriginalValue(Map.Entry<UUID, FactoryBase<?,R>> currentEntry) {
//        if (currentEntry.getValue()==currentData){//for root different id don't make sense
//            return commonData;
//        }
//        return commonMap.get(currentEntry.getKey());
//    }

    public MergeDiffInfo<R> mergeIntoCurrent(Function<String,Boolean> permissionChecker) {
        return createMergeResult(permissionChecker).executeMerge();
    }

}
