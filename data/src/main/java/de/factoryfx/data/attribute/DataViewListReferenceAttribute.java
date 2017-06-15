package de.factoryfx.data.attribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import de.factoryfx.data.Data;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@JsonIgnoreType
public class DataViewListReferenceAttribute<R extends Data, T extends Data> extends ViewListReferenceAttribute<R,T,DataViewListReferenceAttribute<R,T>> {

    public DataViewListReferenceAttribute(Function<R,List<T>> view) {
        super(view);
    }

    @JsonCreator
    DataViewListReferenceAttribute() {
        super(null);
    }

    @Override
    public DataViewListReferenceAttribute internal_copy() {
        return new DataViewListReferenceAttribute<>(view);
    }

}
