package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.factory.FactoryBase;

public class FactoryStyleValidator {

    /** test if the model is valid:
     * all Attributes are public
     * all Attributes not null after instantiation*/
    public Optional<String> validateFactory(FactoryBase<?,?> factoryBase){
        for (Field field: factoryBase.getClass().getDeclaredFields()){

            if (Attribute.class.isAssignableFrom(field.getType())){
                if((field.getModifiers() & Modifier.PUBLIC) != java.lang.reflect.Modifier.PUBLIC) {
                    return Optional.of("should be public: "+ factoryBase.getClass().getName()+"#"+field.getName());
                }
                if((field.getModifiers() & Modifier.FINAL) != java.lang.reflect.Modifier.FINAL) {
                    return Optional.of("should be final: "+ factoryBase.getClass().getName()+"#"+field.getName());
                }

                if (ReferenceAttribute.class==field.getType()) {
                    return Optional.of("should be FactoryReferenceAttribute: "+ factoryBase.getClass().getName()+"#"+field.getName());
                }
                if (ReferenceListAttribute.class==field.getType()) {
                    return Optional.of("should be FactoryListReferenceAttribute: "+ factoryBase.getClass().getName()+"#"+field.getName());
                }

                try {
                    if(field.get(factoryBase)==null) {
                        return Optional.of("should be not null: "+ factoryBase.getClass().getName()+"#"+field.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return Optional.empty();
    }

}
