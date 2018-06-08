package de.factoryfx.factory;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;

/**
 *  provides additional setup for attributes based on the FactoryTreeBuilder
 * @param <R> root
 */
public class FactoryTreeBuilderBasedAttributeSetup<R extends FactoryBase<?,?,R>> {

    private final FactoryTreeBuilder<R> factoryTreeBuilder;

    public FactoryTreeBuilderBasedAttributeSetup(FactoryTreeBuilder<R> factoryTreeBuilder) {
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    public <L, F extends FactoryBase<L, ?, R>> F createNewFactory(Class<F> clazz) {
        //TODO add support for json loaded factory
        return factoryTreeBuilder.buildSubTree(clazz);
    }

    private void setupReferenceAttribute(FactoryReferenceAttribute<?, ?> referenceAttribute) {
        Class<?> referenceClass = referenceAttribute.internal_getReferenceClass();
        Scope scope = factoryTreeBuilder.getScope(referenceClass);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
        if (scope==Scope.PROTOTYPE) {
            referenceAttribute.userNotSelectable();
        }
    }

    private void setupReferenceListAttribute(FactoryReferenceListAttribute<?, ?> referenceAttribute) {
        Class<?> referenceClass = referenceAttribute.internal_getReferenceClass();
        Scope scope = factoryTreeBuilder.getScope(referenceClass);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
        if (scope==Scope.PROTOTYPE) {
            referenceAttribute.userNotSelectable();
        }
    }

    @SuppressWarnings("unchecked")
    public void accept(Attribute<?, ?> attribute) {
        if (attribute instanceof FactoryReferenceAttribute){
            setupReferenceAttribute((FactoryReferenceAttribute)attribute);
        }
        if (attribute instanceof FactoryReferenceListAttribute){
            setupReferenceListAttribute((FactoryReferenceListAttribute)attribute);
        }
    }


}
