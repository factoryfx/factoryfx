package io.github.factoryfx.javafx.data.widget.factorydiff;

import io.github.factoryfx.data.Data;
import io.github.factoryfx.data.attribute.Attribute;
import io.github.factoryfx.data.merge.AttributeDiffInfo;

public class AttributeDiffInfoExtended {
    public final boolean merge;
    public final boolean conflict;
    public final boolean violation;
    public final AttributeDiffInfo attributeDiffInfo;
    public final Data previousRoot;
    public final Data newRoot;

    public AttributeDiffInfoExtended(boolean merge, boolean conflict, boolean violation, AttributeDiffInfo attributeDiffInfo, Data previousRoot, Data newRoot) {
        this.merge = merge;
        this.conflict = conflict;
        this.violation = violation;
        this.attributeDiffInfo = attributeDiffInfo;
        this.previousRoot = previousRoot;
        this.newRoot = newRoot;
    }

    public String parentDisplayText() {
        return attributeDiffInfo.parentDisplayText(newRoot);
    }

    public Attribute<?,?> createPreviousAttribute() {
        return attributeDiffInfo.getAttribute(previousRoot);
    }

    public String getPreviousAttributeDisplayText() {
        return attributeDiffInfo.getAttributeDisplayText(previousRoot);
    }

    public String getNewAttributeDisplayText() {
        return attributeDiffInfo.getAttributeDisplayText(newRoot);
    }
}
