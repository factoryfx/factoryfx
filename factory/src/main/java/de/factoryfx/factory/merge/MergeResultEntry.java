package de.factoryfx.factory.merge;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.Attribute;

public class MergeResultEntry<T extends FactoryBase<?,T>> {
    public final FactoryBase<?,T> parent;
    public List<FactoryBase<?,?>> path;

    public MergeResultEntryInfo mergeResultEntryInfo;

    public MergeResultEntry(FactoryBase<?, T> parent, Attribute<?> attribute, Optional<Attribute<?>> newAttribute, Locale locale) {
        this.parent=parent;

        mergeResultEntryInfo=new MergeResultEntryInfo(attribute.getDisplayText(locale), newAttribute.map((a)->a.getDisplayText(locale)).orElse(""),parent.getDisplayText());
    }



    //    public String getPathDisplayText() {
//        return path.stream().map(pathElement -> pathElement.getDescriptiveName()).collect(Collectors.joining("/"));
//    }


//    public void setPath(List<FactoryBase<?,?>> path) {
//        this.path = path;
//    }
}
