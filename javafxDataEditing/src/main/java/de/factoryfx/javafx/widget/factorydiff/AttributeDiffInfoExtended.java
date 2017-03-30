package de.factoryfx.javafx.widget.factorydiff;

import de.factoryfx.data.merge.AttributeDiffInfo;

public class AttributeDiffInfoExtended {
    public final boolean merge;
    public final boolean conflict;
    public final boolean violation;
    public final AttributeDiffInfo attributeDiffInfo;

    public AttributeDiffInfoExtended(boolean merge, boolean conflict, boolean violation, AttributeDiffInfo attributeDiffInfo) {
        this.merge = merge;
        this.conflict = conflict;
        this.violation = violation;
        this.attributeDiffInfo = attributeDiffInfo;
    }
}
