package de.factoryfx.factory;

import com.sun.xml.internal.bind.v2.TODO;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;

import java.util.function.Consumer;

/**
 *  provides additional setup for attributes based on the FactoryTreeBuilder
 * @param <R>
 */
public class AttributeSetupHelper<R extends FactoryBase<?,?,R>> implements Consumer<Attribute<?,?>> {

    private final FactoryTreeBuilder<R> factoryTreeBuilder;

    public AttributeSetupHelper(FactoryTreeBuilder<R> factoryTreeBuilder) {
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    public <L, F extends FactoryBase<L, ?, R>> F createNewFactory(Class<F> clazz) {
        //TODO add support for json loaded factory
        return factoryTreeBuilder.buildSubTree(clazz);
    }

    public void setupReferenceAttribute(FactoryReferenceAttribute<?, ?> referenceAttribute, Class clazz) {
        Scope scope = factoryTreeBuilder.getScope(clazz);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
        if (scope==Scope.PROTOTYPE) {
            referenceAttribute.userNotSelectable();
        }
    }

    public void setupReferenceListAttribute(FactoryReferenceListAttribute<?, ?> referenceAttribute, Class clazz) {
        Scope scope = factoryTreeBuilder.getScope(clazz);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
        if (scope==Scope.PROTOTYPE) {
            referenceAttribute.userNotSelectable();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void accept(Attribute<?, ?> attribute) {
        if (attribute instanceof FactoryReferenceAttribute){
            ((FactoryReferenceAttribute)attribute).internal_setupWithAttributeSetupHelper(this);
        }
        if (attribute instanceof FactoryReferenceListAttribute){
            ((FactoryReferenceListAttribute)attribute).internal_setupWithAttributeSetupHelper(this);
        }
    }


}
