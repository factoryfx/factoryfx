package de.factoryfx.server.angularjs.model;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.factoryfx.data.merge.MergeDiff;

public class WebGuiMergeDiff {
    public final List<WebGuiMergeDiffInfo> mergeInfos;
    public final List<WebGuiMergeDiffInfo> conflictInfos;

    public WebGuiMergeDiff(MergeDiff mergeDiff, Locale locale){
        mergeInfos = mergeDiff.getMergeInfos().stream().map(mergeResultEntry -> new WebGuiMergeDiffInfo(mergeResultEntry.mergeResultEntryInfo,locale)).collect(Collectors.toList());
        conflictInfos = mergeDiff.getConflictInfos().stream().map(mergeResultEntry -> new WebGuiMergeDiffInfo(mergeResultEntry.mergeResultEntryInfo,locale)).collect(Collectors.toList());

    }

}
