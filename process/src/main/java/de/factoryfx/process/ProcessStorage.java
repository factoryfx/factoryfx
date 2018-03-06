package de.factoryfx.process;

import de.factoryfx.data.Data;

import java.util.ArrayList;
import java.util.List;

public class ProcessStorage<PP extends ProcessParameter> {

    public void store(PP processParameter){

    }

    public List<ProcessParameter> getExecutableProcessParameter(){
        return new ArrayList<>();
    }

}
