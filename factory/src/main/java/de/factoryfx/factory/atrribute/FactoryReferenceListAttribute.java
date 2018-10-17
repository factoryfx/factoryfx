package de.factoryfx.factory.atrribute;

import java.util.*;
import java.util.function.*;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.CopySemantic;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.validation.Validation;
import de.factoryfx.factory.FactoryBase;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryReferenceListAttribute<L, F extends FactoryBase<? extends L,?,?>> extends  ReferenceListAttribute<F,FactoryReferenceListAttribute<L, F>>{

    public FactoryReferenceListAttribute(Class<F> clazz) {
        super();
        setup(clazz);
    }

    public List<L> instances(){
        ArrayList<L> result = new ArrayList<>(this.size());
        for(F item: this){
            result.add(item.internalFactory().instance());
        }
        return result;
    }

    public L instances(Predicate<F> filter){
        Optional<F> any = get().stream().filter(filter).findAny();
        return any.map(t -> t.internalFactory().instance()).orElse(null);
    }

    /** workaround for chained generics (missing java feature)
     * http://openjdk.java.net/jeps/101
     * https://stackoverflow.com/questions/27134626/when-does-diamond-syntax-not-work-in-java-8
     *
     * if possible use constructor instead
     *
     *      * e.g.:
     *      *     public final FactoryReferenceListAttribute&lt;MicroserviceRestClient&lt;VS,RS,S&gt;,MicroserviceRestClientFactory&lt;Void,RichClientRoot,VS,RS,S&gt;&gt; restClient =
     *      *             FactoryReferenceListAttribute.create(new FactoryReferenceListAttribute&lt;&gt;(MicroserviceRestClientFactory.class));
     *
     * @param factoryReferenceListAttribute factoryReferenceListAttribute
     * @return factoryReferenceListAttribute
     * @param <L> Live Object
     * @param <F> Factory
     *
     **/
    @SuppressWarnings("unchecked")
    public static <L, F extends FactoryBase<? extends L,?,?>> FactoryReferenceListAttribute create(FactoryReferenceListAttribute<L,F> factoryReferenceListAttribute){
        return factoryReferenceListAttribute;
    }

    //API workaround for #create, no function just for improved API
    @Override
    public FactoryReferenceListAttribute<L, F> defaultExpanded() {
        return super.defaultExpanded();
    }

    @Override
    public FactoryReferenceListAttribute<L, F> possibleValueProvider(Function<Data, Collection<F>> provider) {
        return super.possibleValueProvider(provider);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> newValuesProvider(BiFunction<Data, FactoryReferenceListAttribute<L, F>, List<F>> newValuesProviderFromRootAndAttribute) {
        return super.newValuesProvider(newValuesProviderFromRootAndAttribute);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> additionalDeleteAction(BiConsumer<F, Data> additionalDeleteAction) {
        return super.additionalDeleteAction(additionalDeleteAction);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> userNotSelectable() {
        return super.userNotSelectable();
    }

    @Override
    public FactoryReferenceListAttribute<L, F> userNotCreatable() {
        return super.userNotCreatable();
    }

    @Override
    public FactoryReferenceListAttribute<L, F> setCopySemantic(CopySemantic copySemantic) {
        return super.setCopySemantic(copySemantic);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> catalogueBased() {
        return super.catalogueBased();
    }

    @Override
    public FactoryReferenceListAttribute<L, F> validation(Validation<List<F>> validation) {
        return super.validation(validation);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> permission(String permission) {
        return super.permission(permission);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> addonText(String addonText) {
        return super.addonText(addonText);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> labelText(String text) {
        return super.labelText(text);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> labelText(String labelText, Locale locale) {
        return super.labelText(labelText, locale);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> en(String text) {
        return super.en(text);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> de(String text) {
        return super.de(text);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> es(String text) {
        return super.es(text);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> fr(String text) {
        return super.fr(text);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> it(String text) {
        return super.it(text);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> pt(String text) {
        return super.pt(text);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> tooltipEn(String tooltip) {
        return super.tooltipEn(tooltip);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> tooltipDe(String tooltip) {
        return super.tooltipDe(tooltip);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> userReadOnly() {
        return super.userReadOnly();
    }

    @Override
    public FactoryReferenceListAttribute<L, F> userReadOnly(Supplier<Boolean> readyOnlySupplier) {
        return super.userReadOnly(readyOnlySupplier);
    }
}
