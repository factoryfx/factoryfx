package io.github.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import io.github.factoryfx.data.Data;

import java.util.function.Function;

/**
 * DataView view are just additional information and navigation help, they have no effect on the data injection <br>
 * They should be never used as constructor parameter<br>
 * see also: <strong>Factory</strong>ViewReferenceAttribute
 */
@JsonIgnoreType
public class DataViewReferenceAttribute<R extends Data, T extends Data> extends ViewReferenceAttribute<R,T,DataViewReferenceAttribute<R,T>> {

    public DataViewReferenceAttribute(Function<R,T> view) {
        super(view);
    }

}
