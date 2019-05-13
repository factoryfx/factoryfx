package io.github.factoryfx.factory;

import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RootFactoryWrapper<R extends FactoryBase<?,R>> {

    private final R rootFactory;

    public RootFactoryWrapper(R rootFactory) {
        this.rootFactory = rootFactory;
        this.rootFactory.internal().finalise();
        this.rootFactory.internal().loopDetector();//loop after that cause views/attributes need root

        updateCachedChildren();
    }

    private List<FactoryBase<?,R>> factoriesInCreateAndStartOrder;
    private List<FactoryBase<?,R>> factoriesInDestroyOrder;
    public void updateCachedChildren(){

        factoriesInCreateAndStartOrder = rootFactory.internal().getFactoriesInCreateAndStartOrder();
        factoriesInDestroyOrder = rootFactory.internal().getFactoriesInDestroyOrder();
    }

    public List<FactoryBase<?,R>> collectChildFactories(){
        return factoriesInCreateAndStartOrder;
    }

    public List<FactoryBase<?,R>> getFactoriesInCreateAndStartOrder(){
        return factoriesInCreateAndStartOrder;
    }


    public List<FactoryBase<?,R>> getFactoriesInDestroyOrder(){
        return factoriesInDestroyOrder;
    }

    public void determineRecreationNeedFromRoot(Set<FactoryBase<?,R>> changedData) {
        rootFactory.internal().determineRecreationNeedFromRoot(changedData);
    }

    public static class MergeResult<R extends FactoryBase<?,R>> {
        public final MergeDiffInfo<R> mergeDiffInfo;
        public final Set<FactoryBase<?,R>> mergedFactories;

        private MergeResult(MergeDiffInfo<R> mergeDiffInfo, Set<FactoryBase<?, R>> mergedFactories) {
            this.mergeDiffInfo = mergeDiffInfo;
            this.mergedFactories = mergedFactories;
        }
    }

    public MergeResult<R> merge(R commonVersion, R newVersion, Function<String,Boolean> permissionChecker){
        DataMerger<R> dataMerger = new DataMerger<>(rootFactory, commonVersion, newVersion);
        io.github.factoryfx.factory.merge.MergeResult<R> mergeResult = dataMerger.createMergeResult(permissionChecker);
        MergeDiffInfo<R> result = mergeResult.executeMerge();
        mergeResult.getMergedFactories();
        rootFactory.internal().loopDetector();
        updateCachedChildren();
        return new MergeResult<R>(result,mergeResult.getMergedFactories());
    }

    /** copy a root data element
     * @return root copy
     */
    public R copy(){
        return rootFactory.internal().copy();
    }

    public String logStartDisplayTextDeep() {
        return rootFactory.internal().logStartDisplayTextDeep();
    }

    public String logUpdateDisplayTextDeep() {
        return rootFactory.internal().logUpdateDisplayTextDeep();
    }

    public R getRoot() {
        return rootFactory;
    }

}
