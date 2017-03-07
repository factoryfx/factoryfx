package de.factoryfx.server.angularjs.model;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.factoryfx.data.merge.MergeDiffInfo;

public class WebGuiMergeDiff {
    public final List<WebGuiMergeDiffInfo> mergeInfos;
    public final List<WebGuiMergeDiffInfo> conflictInfos;

    public WebGuiMergeDiff(MergeDiffInfo mergeDiffInfo, Locale locale){
        mergeInfos = mergeDiffInfo.mergeInfos.stream().map(mergeResultEntry -> new WebGuiMergeDiffInfo(mergeResultEntry,locale)).collect(Collectors.toList());
        conflictInfos = mergeDiffInfo.conflictInfos.stream().map(mergeResultEntry -> new WebGuiMergeDiffInfo(mergeResultEntry,locale)).collect(Collectors.toList());

    }

}
