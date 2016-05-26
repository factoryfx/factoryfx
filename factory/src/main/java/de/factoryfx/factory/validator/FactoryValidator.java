package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.attribute.Attribute;

public class FactoryValidator {

    /** test if the model is valid:
     * all Attributes are public
     * all Attributes not null after instantiation*/
    public Optional<String> validateModel(FactoryBase<?,?> factoryBase){
        for (Field field: factoryBase.getClass().getDeclaredFields()){
            System.out.println(field.getName());
            if (Attribute.class.isAssignableFrom(field.getType())){
                if((field.getModifiers() & Modifier.PUBLIC) != java.lang.reflect.Modifier.PUBLIC) {
                    return Optional.of("should be public "+ factoryBase.getClass().getName()+field.getName());
                }
                try {
                    if(field.get(factoryBase)==null) {
                        return Optional.of("should be not null "+ factoryBase.getClass().getName()+field.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return Optional.empty();
    }
}
