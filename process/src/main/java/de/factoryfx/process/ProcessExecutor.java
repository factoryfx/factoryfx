package de.factoryfx.process;

/**
 *
 * @param <V>  Visitor
 * @param <PP> process parameter
 */
public class ProcessExecutor<V,PP extends ProcessParameter> {

    private final ProcessStorage<PP> processStorage;

    public ProcessExecutor(ProcessStorage<PP> processStorage) {
        this.processStorage = processStorage;
    }


    public void create(PP parameter){


    }




}
