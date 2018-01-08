package de.factoryfx.data.storage;

import de.factoryfx.data.Data;
import de.factoryfx.data.merge.MergeDiffInfo;

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
