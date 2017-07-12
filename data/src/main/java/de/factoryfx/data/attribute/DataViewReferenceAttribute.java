package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.factoryfx.data.Data;

import java.util.function.Function;

/**
 * DataView view are just additional information and navigation help, they have no effect on the datainjection <br/>
 * They should be never used as constructor parameter<br/>
 * see also: <strong>Factory</strong>ViewReferenceAttribute
 */
@JsonIgnoreType
public class DataViewReferenceAttribute<R extends Data, T extends Data> extends ViewReferenceAttribute<R,T,DataViewReferenceAttribute<R,T>> {

    public DataViewReferenceAttribute(Function<R,T> view) {
        super(view);
    }

    @JsonCreator
    DataViewReferenceAttribute() {
        super(null);
    }


}
