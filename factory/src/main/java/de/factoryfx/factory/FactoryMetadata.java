package de.factoryfx.factory;

import java.util.HashMap;
import java.util.function.Function;

public class FactoryMetadata {

//    private String permission;
//    private Function<Object,String> labelProvider= t -> t.getClass().getSimpleName()+":"+((FactoryBase)t).getId();

    private static class FactoryMetadataData{
        public String permission;
        public Function<Object,String> labelProvider= t -> t.getClass().getSimpleName()+":"+((FactoryBase)t).getId();
    }

    HashMap<Class<?>,FactoryMetadataData> classes=new HashMap<>();
//
//    public String permission(){
//
//    }

    @SuppressWarnings("unchecked")
    public <T extends FactoryBase> void setDisplayTextProvider(Function<T,String> provider, Class<T> clazz) {
        FactoryMetadataData factoryMetadataData = classes.get(clazz);
        if (factoryMetadataData==null){
            factoryMetadataData=new FactoryMetadataData();
            classes.put(clazz,factoryMetadataData);
        }

        factoryMetadataData.labelProvider= o -> provider.apply(((T)o));
    }

    FactoryMetadataData defaultFactoryMetadataData=new FactoryMetadataData();
    public String getDisplayText(FactoryBase factory, Class<?> clazz) {
        FactoryMetadataData factoryMetadataData = classes.get(clazz);
        if (factoryMetadataData==null){
            factoryMetadataData= defaultFactoryMetadataData;
        }
        return factoryMetadataData.labelProvider.apply(factory);
    }
}
