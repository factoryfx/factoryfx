package de.factoryfx.data.merge;

import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;

public class MergeResultEntry {
    public String requiredPermission;

    private final MergeResultEntryInfo mergeResultEntryInfo;

    public MergeResultEntry(String parentDisplayText, Attribute<?> attribute, Optional<Attribute<?>> newAttribute) {
        this.requiredPermission=attribute.metadata.permission;

        //created here cause attribute ist updated later
        mergeResultEntryInfo=new MergeResultEntryInfo(attribute.getDisplayText(), newAttribute.map((a)->a.getDisplayText()).orElse(""), attribute.metadata.labelText, parentDisplayText);
    }

    public MergeResultEntryInfo createInfo(boolean conflict){
        mergeResultEntryInfo.conflict=conflict;
        return mergeResultEntryInfo;
    }

}
