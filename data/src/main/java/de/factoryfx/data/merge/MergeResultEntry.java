package de.factoryfx.data.merge;

import java.util.Optional;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;

public class MergeResultEntry {
    public final Data parent;
    public String requiredPermission;

    private final Attribute<?> attribute;
    private final Optional<Attribute<?>> newAttribute;

    public MergeResultEntry(Data parent, Attribute<?> attribute, Optional<Attribute<?>> newAttribute) {
        this.parent=parent;
        this.requiredPermission=attribute.metadata.permisson;

        this.attribute=attribute;
        this.newAttribute=newAttribute;

//        mergeResultEntryInfo=new MergeResultEntryInfo(attribute.getDisplayText(), newAttribute.map((a)->a.getDisplayText()).orElse(""), attribute.metadata.labelText, parent.internal().getDisplayText());
    }

    public MergeResultEntryInfo createInfo(boolean conflict){
        return new MergeResultEntryInfo(attribute.getDisplayText(), newAttribute.map((a)->a.getDisplayText()).orElse(""), attribute.metadata.labelText, parent.internal().getDisplayText(),conflict);
    }


    //    public String getPathDisplayText() {
//        return path.stream().map(pathElement -> pathElement.getDescriptiveName()).collect(Collectors.joining("/"));
//    }


//    public void setPath(List<FactoryBase<?,?>> path) {
//        this.path = path;
//    }
}
