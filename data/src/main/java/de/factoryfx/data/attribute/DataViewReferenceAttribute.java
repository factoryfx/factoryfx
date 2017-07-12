package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.factoryfx.data.Data;

import java.util.function.Function;

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
