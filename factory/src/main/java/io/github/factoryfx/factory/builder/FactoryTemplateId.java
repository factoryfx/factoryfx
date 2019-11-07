package io.github.factoryfx.factory.builder;

import io.github.factoryfx.factory.FactoryBase;

import java.util.Objects;

/**
 * describes a factory template in a {@link FactoryTreeBuilder}
 * must be unique in {@link FactoryTreeBuilder}
 * @param <R>
 * @param <F>
 */
public class FactoryTemplateId<R extends FactoryBase<?,R>,F extends FactoryBase<?,R>> {
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
    }

    /**
     * @param clazz class, null for any
     * @param name name, null for any
     */
    public FactoryTemplateId(Class<F> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
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
}
