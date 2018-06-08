package de.factoryfx.factory;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.DataMerger;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.log.FactoryLogEntryTreeItem;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RootFactoryWrapper<R extends FactoryBase<?,?,?>> {

    private final R rootFactory;

    public RootFactoryWrapper(R rootFactory) {
        this.rootFactory = rootFactory;
        this.rootFactory.internal().addBackReferences();
        this.rootFactory.internalFactory().loopDetector();//loop after that cause views/attributes need root and

        updateCachedChildren();
    }

    private List<FactoryBase<?,?,?>> factoriesInCreateAndStartOrder;
    private List<FactoryBase<?,?,?>> factoriesInDestroyOrder;
    public void updateCachedChildren(){

        factoriesInCreateAndStartOrder = rootFactory.internalFactory().getFactoriesInCreateAndStartOrder();
        factoriesInDestroyOrder = rootFactory.internalFactory().getFactoriesInDestroyOrder();
    }

    public List<FactoryBase<?,?,?>> collectChildFactories(){
        return factoriesInCreateAndStartOrder;
    }

    public List<FactoryBase<?,?,?>> getFactoriesInCreateAndStartOrder(){
        return factoriesInCreateAndStartOrder;
    }


    public List<FactoryBase<?,?,?>> getFactoriesInDestroyOrder(){
        return factoriesInDestroyOrder;
    }

    public void determineRecreationNeedFromRoot(Set<Data> changedData) {
        rootFactory.internalFactory().determineRecreationNeedFromRoot(changedData);
    }

    public MergeDiffInfo<R> merge(R commonVersion, R newVersion, Function<String,Boolean> permissionChecker){
        DataMerger<R> dataMerger = new DataMerger<>(rootFactory, commonVersion, newVersion);
        MergeDiffInfo<R> mergeDiffInfo = dataMerger.mergeIntoCurrent(permissionChecker);
        rootFactory.internalFactory().loopDetector();
        updateCachedChildren();
        return mergeDiffInfo;
    }

    /** copy a root data element
     * @return root copy
     */
    public R copy(){
        return rootFactory.internal().copy();
    }

    public String logDisplayTextDeep() {
        return rootFactory.internalFactory().logDisplayTextDeep();
    }

    public R getRoot() {
        return rootFactory;
    }

    public FactoryLogEntryTreeItem createFactoryLogTree() {
        return rootFactory.internalFactory().createFactoryLogTree();
    }
}
