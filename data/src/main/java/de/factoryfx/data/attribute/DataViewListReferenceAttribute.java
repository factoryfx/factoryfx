package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.factoryfx.data.Data;

import java.util.*;
import java.util.function.Function;

/**
 * DataView view are just additional information and navigation help, they have no effect on the datainjection <br>
 * They should be never used as constructor parameter<br>
 * see also: <strong>Factory</strong>ViewListReferenceAttribute
 */
@JsonIgnoreType
public class DataViewListReferenceAttribute<R extends Data, T extends Data> extends ViewListReferenceAttribute<R,T,DataViewListReferenceAttribute<R,T>> {

    public DataViewListReferenceAttribute(Function<R,List<T>> view) {
        super(view);
    }

}
