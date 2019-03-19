package io.github.factoryfx.data.storage;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.merge.MergeDiffInfo;

public interface ChangeSummaryCreator<R extends Data,S> {
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
