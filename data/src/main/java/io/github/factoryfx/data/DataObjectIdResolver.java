package io.github.factoryfx.data;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;

/**
 * Exception if id is not resolvable, migration can fix id resolution affected from removed attributes
 */
public class DataObjectIdResolver extends SimpleObjectIdResolver {

    public DataObjectIdResolver() { }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType.getClass() == getClass();
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return new DataObjectIdResolver();
    }

    @Override
    public Object resolveId(ObjectIdGenerator.IdKey id) {
        Object resolve = (_items == null) ? null : _items.get(id);;
        if (resolve==null){
            throw new UnresolvableJsonIDException(id.key.toString());
        }
        return resolve;
    }

    public static class UnresolvableJsonIDException extends RuntimeException{
        public final String id;

        public UnresolvableJsonIDException(String id) {
            this.id = id;
        }
    }
}
