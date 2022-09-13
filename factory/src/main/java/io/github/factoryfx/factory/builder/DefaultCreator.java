package io.github.factoryfx.factory.builder;

import java.util.function.BiConsumer;
import java.util.function.Function;

import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryPolymorphicAttribute;
import io.github.factoryfx.factory.metadata.FactoryMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

public class DefaultCreator<F extends FactoryBase<?, R>, R extends FactoryBase<?, R>> implements Function<FactoryContext<R>, F> {
    private final Class<F> clazz;
    private final BiConsumer<F, FactoryContext<R>> manualConfig;
    private final boolean fillNullable;

    public DefaultCreator(Class<F> clazz) {
        this(clazz, (a, b) -> {}, false);
    }

    public DefaultCreator(Class<F> clazz, boolean fillNullable) {
        this(clazz, (a, b) -> {}, fillNullable);
    }

    public DefaultCreator(Class<F> clazz, BiConsumer<F, FactoryContext<R>> manualConfig) {
        this(clazz, manualConfig, false);
    }

    public DefaultCreator(Class<F> clazz, BiConsumer<F, FactoryContext<R>> manualConfig, boolean fillNullable) {
        this.clazz = clazz;
        this.manualConfig = manualConfig;
        this.fillNullable = fillNullable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public F apply(FactoryContext<R> context) {
        FactoryMetadata<R, F> factoryMetadata = FactoryMetadataManager.getMetadata(clazz);
        F result = factoryMetadata.newInstance();
        this.manualConfig.accept(result, context);

        result.internal().visitAttributesFlat((attributeMetadata, attribute) -> {
            if (attribute instanceof FactoryPolymorphicAttribute factoryPolymorphicAttribute
                && (fillNullable || factoryPolymorphicAttribute.internal_required())
                && !factoryPolymorphicAttribute.internal_isCatalogueBased()
                && factoryPolymorphicAttribute.get() == null) {
                if (attributeMetadata.liveObjectClass != null) {
                    for (FactoryBase<?, R> factory : context.getListFromLiveObjectClass(attributeMetadata.liveObjectClass, clazz)) {
                        factoryPolymorphicAttribute.set(factory);
                        break;
                    }
                }
                //TODO multiple factories seems wrong, throw exception to enforce for uniqueness?
                return;
            }

            if (attribute instanceof FactoryBaseAttribute factoryBaseAttribute
                && (fillNullable || factoryBaseAttribute.internal_required())
                && !factoryBaseAttribute.internal_isCatalogueBased()
                && factoryBaseAttribute.get() == null) {
                Class<? extends FactoryBase<?, ?>> clazz = attributeMetadata.referenceClass;
                validateAttributeClass(attributeMetadata.attributeVariableName, clazz);
                FactoryBase<?, ?> factoryBase = context.getUnchecked(clazz);
                factoryBaseAttribute.set(factoryBase);
                if (factoryBaseAttribute.internal_required()) {
                    if (factoryBaseAttribute.get() == null) {
                        throw new IllegalStateException(
                            "\nbuilder missing Factory: " + attributeMetadata.liveObjectClass + "\n" +
                                "required in: " + clazz + "\n" +
                                "from attribute: " + attributeMetadata.attributeVariableName
                        );
                    }
                }
            }
        });
        return result;

    }

    protected void validateAttributeClass(String attributeVariableName, Class<? extends FactoryBase<?, ?>> clazz) {
        if (clazz == null) {
            throw new IllegalStateException("cant build Factory " + this.clazz + ". Attribute: '" + attributeVariableName + "' missing factory clazz info");
        }
    }
}
