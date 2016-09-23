package de.factoryfx.factory.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.data.attribute.Attribute;

public class FactoryStyleValidator {

    /** test if the model is valid:
     * all Attributes are public
     * all Attributes not null after instantiation*/
    public Optional<String> validateFactory(FactoryBase<?> factoryBase){
        for (Field field: factoryBase.getClass().getDeclaredFields()){

            if (Attribute.class.isAssignableFrom(field.getType())){
                if((field.getModifiers() & Modifier.PUBLIC) != java.lang.reflect.Modifier.PUBLIC) {
                    return Optional.of("should be public "+ factoryBase.getClass().getName()+"#"+field.getName());
                }
                if((field.getModifiers() & Modifier.FINAL) != java.lang.reflect.Modifier.FINAL) {
                    return Optional.of("should be final "+ factoryBase.getClass().getName()+"#"+field.getName());
                }
                try {
                    if(field.get(factoryBase)==null) {
                        return Optional.of("should be not null "+ factoryBase.getClass().getName()+"#"+field.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return Optional.empty();
    }

    public Optional<String> validateLiveObject(Class<? extends LiveObject> liveObject){
        for (Constructor<?> constructor: liveObject.getDeclaredConstructors()){
            for (Class<?> constructorParameterClass: constructor.getParameterTypes()){
                if (FactoryBase.class.isAssignableFrom(constructorParameterClass)){
                    return Optional.of("constructorParameterClass should not be a Factory"+ liveObject.getName());
                }
            }
            System.out.println("\n");
        }

        return Optional.empty();
    }

}
