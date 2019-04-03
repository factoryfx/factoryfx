package io.github.factoryfx.factory;

import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.MergeDiffInfo;
import io.github.factoryfx.factory.log.FactoryLogEntryTreeItem;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RootFactoryWrapper<R extends FactoryBase<?,R>> {

    private final R rootFactory;

    public RootFactoryWrapper(R rootFactory) {
        this.rootFactory = rootFactory;
        this.rootFactory.internal().addBackReferences();
        this.rootFactory.internal().loopDetector();//loop after that cause views/attributes need root and

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

    public void determineRecreationNeedFromRoot(Set<FactoryBase<?,?>> changedData) {
        rootFactory.internal().determineRecreationNeedFromRoot(changedData);
    }

    public MergeDiffInfo<R> merge(R commonVersion, R newVersion, Function<String,Boolean> permissionChecker){
        DataMerger<R> dataMerger = new DataMerger<>(rootFactory, commonVersion, newVersion);
        MergeDiffInfo<R> mergeDiffInfo = dataMerger.mergeIntoCurrent(permissionChecker);
        rootFactory.internal().loopDetector();
        updateCachedChildren();
        return mergeDiffInfo;
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

    public FactoryLogEntryTreeItem createFactoryLogTree() {
        return rootFactory.internal().createFactoryLogTree();
    }
}
