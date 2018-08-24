package de.factoryfx.factory.atrribute;

import com.fasterxml.jackson.annotation.JsonCreator;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.CopySemantic;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.factory.FactoryBase;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryReferenceAttribute<L, F extends FactoryBase<? extends L,?,?>> extends FactoryReferenceBaseAttribute<L,F,FactoryReferenceAttribute<L, F>> {

    public FactoryReferenceAttribute(Class<F> clazz) {
        setup(clazz);
    }


    /** workaround for chained generics (missing java feature)
     * http://openjdk.java.net/jeps/101
     * https://stackoverflow.com/questions/27134626/when-does-diamond-syntax-not-work-in-java-8
     *
     * if possible use constructor instead
     * e.g.:
     *     public final FactoryReferenceAttribute<MicroserviceRestClient<VS,RS,S>,MicroserviceRestClientFactory<Void,RichClientRoot,VS,RS,S>> restClient =
     *             FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(MicroserviceRestClientFactory.class));
     *
     * @param factoryReferenceAttribute factoryReferenceAttribute
     * @return factoryReferenceListAttribute
     * @param <L> Live Object
     * @param <F> Factory
     * */
    @SuppressWarnings("unchecked")
    public static <L, F extends FactoryBase<? extends L,?,?>> FactoryReferenceAttribute create(FactoryReferenceAttribute<L,F> factoryReferenceAttribute){
        return factoryReferenceAttribute;
    }

    //API workaround for #create, no function just for improved API
    @Override
    public FactoryReferenceAttribute<L, F> nullable() {
        return super.nullable();
    }

    @Override
    public FactoryReferenceAttribute<L, F> possibleValueProvider(Function<Data, Collection<F>> provider) {
        return super.possibleValueProvider(provider);
    }

    @Override
    public FactoryReferenceAttribute<L, F> newValueProvider(Function<Data, F> newValueProviderFromRoot) {
        return super.newValueProvider(newValueProviderFromRoot);
    }

    @Override
    public FactoryReferenceAttribute<L, F> newValuesProvider(BiFunction<Data, FactoryReferenceAttribute<L, F>, List<F>> newValuesProviderFromRootAndAttribute) {
        return super.newValuesProvider(newValuesProviderFromRootAndAttribute);
    }

    @Override
    public FactoryReferenceAttribute<L, F> additionalDeleteAction(BiConsumer<F, Data> additionalDeleteAction) {
        return super.additionalDeleteAction(additionalDeleteAction);
    }

    @Override
    public FactoryReferenceAttribute<L, F> userNotSelectable() {
        return super.userNotSelectable();
    }

    @Override
    public FactoryReferenceAttribute<L, F> userNotCreatable() {
        return super.userNotCreatable();
    }

    @Override
    public FactoryReferenceAttribute<L, F> setCopySemantic(CopySemantic copySemantic) {
        return super.setCopySemantic(copySemantic);
    }

    @Override
    public FactoryReferenceAttribute<L, F> catalogueBased() {
        return super.catalogueBased();
    }

    @Override
    public FactoryReferenceAttribute<L, F> validation(Validation<F> validation) {
        return super.validation(validation);
    }

    @Override
    public FactoryReferenceAttribute<L, F> permission(String permission) {
        return super.permission(permission);
    }

    @Override
    public FactoryReferenceAttribute<L, F> addonText(String addonText) {
        return super.addonText(addonText);
    }

    @Override
    public FactoryReferenceAttribute<L, F> labelText(String text) {
        return super.labelText(text);
    }

    @Override
    public FactoryReferenceAttribute<L, F> labelText(String labelText, Locale locale) {
        return super.labelText(labelText, locale);
    }

    @Override
    public FactoryReferenceAttribute<L, F> en(String text) {
        return super.en(text);
    }

    @Override
    public FactoryReferenceAttribute<L, F> de(String text) {
        return super.de(text);
    }

    @Override
    public FactoryReferenceAttribute<L, F> es(String text) {
        return super.es(text);
    }

    @Override
    public FactoryReferenceAttribute<L, F> fr(String text) {
        return super.fr(text);
    }

    @Override
    public FactoryReferenceAttribute<L, F> it(String text) {
        return super.it(text);
    }

    @Override
    public FactoryReferenceAttribute<L, F> pt(String text) {
        return super.pt(text);
    }

    @Override
    public FactoryReferenceAttribute<L, F> tooltipEn(String tooltip) {
        return super.tooltipEn(tooltip);
    }

    @Override
    public FactoryReferenceAttribute<L, F> tooltipDe(String tooltip) {
        return super.tooltipDe(tooltip);
    }

    @Override
    public FactoryReferenceAttribute<L, F> userReadOnly() {
        return super.userReadOnly();
    }

    @Override
    public FactoryReferenceAttribute<L, F> userReadOnly(Supplier<Boolean> readyOnlySupplier) {
        return super.userReadOnly(readyOnlySupplier);
    }
}
