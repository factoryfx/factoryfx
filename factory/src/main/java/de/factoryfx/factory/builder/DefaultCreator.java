package de.factoryfx.factory.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;

public class DefaultCreator<F extends FactoryBase<?,?,R>, R extends FactoryBase<?,?,R>> implements Function<FactoryContext<R>, F> {
    private final Class<F> clazz;

    public DefaultCreator(Class<F> clazz) {
        this.clazz = clazz;
    }


    @SuppressWarnings("unchecked")
    @Override
    public F apply(FactoryContext<R> context) {
        try {

            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            F result = (F) constructor.newInstance(new Object[0]);


            result.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                if (attribute instanceof FactoryReferenceAttribute){
                    FactoryReferenceAttribute factoryReferenceAttribute = (FactoryReferenceAttribute) attribute;
                    Class<? extends FactoryBase> clazz = factoryReferenceAttribute.internal_getReferenceClass();
                    validateAttributeClass(attributeVariableName, clazz);
                    FactoryBase factoryBase = context.get(clazz);
                    factoryReferenceAttribute.set(factoryBase);
                }
                if (attribute instanceof FactoryReferenceListAttribute){
                    FactoryReferenceListAttribute factoryReferenceAttribute = (FactoryReferenceListAttribute) attribute;
                    Class<? extends FactoryBase> clazz = factoryReferenceAttribute.internal_getReferenceClass();
                    validateAttributeClass(attributeVariableName, clazz);
                    factoryReferenceAttribute.set(context.getList(clazz));
                }
                if (attribute instanceof FactoryPolymorphicReferenceAttribute){
                    FactoryPolymorphicReferenceAttribute factoryReferenceAttribute = (FactoryPolymorphicReferenceAttribute) attribute;
                    for (Class<? extends FactoryBase> possibleClazz: (List<Class>)factoryReferenceAttribute.internal_possibleFactoriesClasses()){
                        if (context.anyMatch(possibleClazz)){
                            factoryReferenceAttribute.set(context.get(possibleClazz));
                            break;
                        }
                    }
                }
                if (attribute instanceof FactoryPolymorphicReferenceListAttribute){
                    FactoryPolymorphicReferenceListAttribute factoryReferenceAttribute = (FactoryPolymorphicReferenceListAttribute) attribute;
                    for (Class<? extends FactoryBase> possibleClazz: (List<Class>)factoryReferenceAttribute.internal_possibleFactoriesClasses()){
                        if (context.anyMatch(possibleClazz)){
                            factoryReferenceAttribute.add(context.get(possibleClazz));
                            break;
                        }
                    }
                }
                if (attribute instanceof ParametrizedObjectCreatorAttribute){
                    ParametrizedObjectCreatorAttribute factoryReferenceAttribute = (ParametrizedObjectCreatorAttribute) attribute;
                    Class<? extends FactoryBase> clazz = factoryReferenceAttribute.internal_getReferenceClass();
                    validateAttributeClass(attributeVariableName, clazz);
                    FactoryBase factoryBase = context.get(clazz);
                    factoryReferenceAttribute.set(factoryBase);
                }


            });
            return result;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected void validateAttributeClass(String attributeVariableName, Class<? extends FactoryBase> clazz) {
        if (clazz==null){
            throw new IllegalStateException("cant build Factory "+this.clazz+". Attribute: '"+attributeVariableName+"' missing clazz info(setup method)");
        }
    }
}
