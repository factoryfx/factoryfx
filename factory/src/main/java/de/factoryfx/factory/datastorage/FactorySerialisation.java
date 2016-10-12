package de.factoryfx.factory.datastorage;

import de.factoryfx.factory.FactoryBase;

public class FactorySerialisation<T extends FactoryBase<?,?>> {
    public Class<T> rootClazz;


    public T read(int dataModelVersion, String data){
        return null;
    }

    public String write(T root, int dataModelVersion){
        return null;
    }
}
