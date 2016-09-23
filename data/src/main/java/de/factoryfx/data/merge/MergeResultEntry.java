package de.factoryfx.data.merge;

import java.util.Locale;
import java.util.Optional;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;

public class MergeResultEntry<T extends Data> {
    public final Data parent;
    public String requiredPermission;

    public MergeResultEntryInfo mergeResultEntryInfo;

    public MergeResultEntry(Data parent, Attribute<?> attribute, Optional<Attribute<?>> newAttribute, Locale locale) {
        this.parent=parent;
        this.requiredPermission=attribute.metadata.permisson;

        mergeResultEntryInfo=new MergeResultEntryInfo(attribute.getDisplayText(), newAttribute.map((a)->a.getDisplayText()).orElse(""), attribute.metadata.labelText.getPreferred(locale), parent.getDisplayText());
    }


    //    public String getPathDisplayText() {
//        return path.stream().map(pathElement -> pathElement.getDescriptiveName()).collect(Collectors.joining("/"));
//    }


//    public void setPath(List<FactoryBase<?,?>> path) {
//        this.path = path;
//    }
}
