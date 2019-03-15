package de.factoryfx.factory.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.*;
import de.factoryfx.factory.parametrized.ParametrizedObjectCreatorAttribute;

public class DefaultCreator<F extends FactoryBase<?,R>, R extends FactoryBase<?,R>> implements Function<FactoryContext<R>, F> {
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
                if (attribute instanceof FactoryReferenceAttribute || attribute instanceof ParametrizedObjectCreatorAttribute){
                    FactoryReferenceBaseAttribute factoryReferenceAttribute = (FactoryReferenceBaseAttribute) attribute;
                    Class<? extends FactoryBase> clazz = factoryReferenceAttribute.internal_getReferenceClass();
                    validateAttributeClass(attributeVariableName, clazz);
                    if (factoryReferenceAttribute.internal_required()){
                        context.check(this.clazz, attributeVariableName,clazz);
                    }
                    FactoryBase factoryBase = context.getUnchecked(clazz);
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
            });
            return result;
        } catch (InstantiationException  | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("\nto fix the error add jpms boilerplate, \noption 1: module-info.info: opens "+clazz.getPackage().getName()+";\noption 2: open all, open module {A} { ... } (open keyword before module)\n",e);
        }
    }

    protected void validateAttributeClass(String attributeVariableName, Class<? extends FactoryBase> clazz) {
        if (clazz==null){
            throw new IllegalStateException("cant build Factory "+this.clazz+". Attribute: '"+attributeVariableName+"' missing clazz info(setup method)");
        }
    }
}
