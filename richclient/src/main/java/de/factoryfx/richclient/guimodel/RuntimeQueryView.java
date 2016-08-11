package de.factoryfx.richclient.guimodel;

import java.util.List;
import java.util.function.Function;

public class RuntimeQueryView<R> {
    public final String name;
    private final Function<String,List<R>> dataProvider;
    public final Table<R> table;

    public RuntimeQueryView(String name, Function<String, List<R>> dataProvider, Table<R> table) {
        this.name = name;
        this.dataProvider = dataProvider;
        this.table = table;
    }

    public List<R> getData(String filter){
        return dataProvider.apply(filter);
    }

}
