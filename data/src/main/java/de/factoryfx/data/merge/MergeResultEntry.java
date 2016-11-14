package de.factoryfx.data.merge;

import java.util.Optional;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;

public class MergeResultEntry {
    public final Data parent;
    public String requiredPermission;

    private final MergeResultEntryInfo mergeResultEntryInfo;

    public MergeResultEntry(Data parent, Attribute<?> attribute, Optional<Attribute<?>> newAttribute) {
        this.parent=parent;
        this.requiredPermission=attribute.metadata.permisson;

        //created here cause attribute ist updatted later
        mergeResultEntryInfo=new MergeResultEntryInfo(attribute.getDisplayText(), newAttribute.map((a)->a.getDisplayText()).orElse(""), attribute.metadata.labelText, parent.internal().getDisplayText());
    }

    public MergeResultEntryInfo createInfo(boolean conflict){
        mergeResultEntryInfo.conflict=conflict;
        return mergeResultEntryInfo;
    }


    //    public String getPathDisplayText() {
//        return path.stream().map(pathElement -> pathElement.getDescriptiveName()).collect(Collectors.joining("/"));
//    }


//    public void setPath(List<FactoryBase<?,?>> path) {
//        this.path = path;
//    }
}
