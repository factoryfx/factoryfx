package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.FactoryBase;

import java.util.Objects;

/**
 * describes a factory template in a {@link FactoryTreeBuilder}
 * must be unique in {@link FactoryTreeBuilder}
 * @param <F> factory
 */
public class FactoryTemplateId<F extends FactoryBase<?,?>> {
    public final Class<F> clazz;
    public final String name;

    /**
     * workaround constructor for generic types with nested generic parameters ( e.g Class<Example<Example>>  doesn't work)
     * @param name name
     * @param clazz class
     */
    @SuppressWarnings("unchecked")
    public FactoryTemplateId(String name, Class<?> clazz) {
        this.clazz = (Class<F>) clazz;
        this.name = name;

        if (name==null && clazz==null){
            throw new IllegalArgumentException("both parameter are null");
        }
    }

    /**
     * @param clazz class, null for any
     * @param name name, null for any
     */
    public FactoryTemplateId(Class<F> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public FactoryTemplateId(F factory) {
        this.clazz = factory.internal().isTreeBuilderClassUsed()? (Class<F>) factory.getClass() :null;
        this.name = factory.internal().getTreeBuilderName();;
    }

    public boolean match(Class<?> clazzMatch, String name) {
        if (clazzMatch==null) {
            return Objects.equals(this.name,name);
        }
        return clazz==clazzMatch && Objects.equals(this.name,name);
    }

    public boolean match(Class<?> clazzMatch) {
        return clazz==clazzMatch;
    }

    public boolean isDuplicate(FactoryTemplateId templateId) {
        if (name==null && templateId.name==null) {
            return clazz==templateId.clazz;
        }
        if (name==null){
            return false;
        }
        return clazz==templateId.clazz && name.equals(templateId.name);
    }

    public void serializeTo(F factory) {
        factory.internal().setTreeBuilderName(name);
        factory.internal().setTreeBuilderClassUsed(clazz!=null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactoryTemplateId<?> that = (FactoryTemplateId<?>) o;
        return Objects.equals(clazz, that.clazz) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, name);
    }
}
