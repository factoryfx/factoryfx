package io.github.factoryfx.factory.storage;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.merge.MergeDiffInfo;

public interface ChangeSummaryCreator<R extends FactoryBase<?,?>,S> {
    /**
     * for history data
     * @param mergeDiffInfo  diff
     * @return change summary
     */
    S createChangeSummary(MergeDiffInfo<R> mergeDiffInfo);

    /**
     * for future data
     * @param mergeDiffInfo  diff
     * @return change summary
     */
    default S createFutureChangeSummary(MergeDiffInfo<R> mergeDiffInfo){
        return createChangeSummary(mergeDiffInfo);
    }
}
