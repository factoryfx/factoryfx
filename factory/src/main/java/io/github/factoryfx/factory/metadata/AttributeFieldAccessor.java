package io.github.factoryfx.factory.metadata;

import io.github.factoryfx.factory.AttributeVisitor;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;

import java.lang.invoke.MethodHandle;

class AttributeFieldAccessor<R extends FactoryBase<?,R>, L,F extends FactoryBase<L,R>,A> {
    //currently method handles are not faster than reflection but that may change in future versions, https://www.optaplanner.org/blog/2018/01/09/JavaReflectionButMuchFaster.html
    //LambdaMetafactory doesn't work for direct field access

    private final MethodHandle methodHandle;
    private final String name;

    public AttributeFieldAccessor(MethodHandle methodHandle, String name) {
        this.methodHandle = methodHandle;
        this.name = name;
    }


    public void visit(F data, AttributeVisitor attributeVisitor){
        try {
            attributeVisitor.accept(name, (Attribute<?, ?>) methodHandle.invoke(data));
        } catch (IllegalAccessException e) {
                throw new RuntimeException("\nto fix the error add jpms boilerplate, \noption 1: module-info.info: opens "+data.getClass().getPackage().getName()+";\noption 2: open all, open module {A} { ... } (open keyword before module)\n",e);
            } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @SuppressWarnings("unchecked")
    public A get(F data) {
        try {
            return (A)(Attribute<?, ?>)methodHandle.invoke(data);//redundant cast important for performance

        } catch (IllegalAccessException e) {
            throw new RuntimeException("\nto fix the error add jpms boilerplate, \noption 1: module-info.info: opens "+data.getClass().getPackage().getName()+";\noption 2: open all, open module {A} { ... } (open keyword before module)\n",e);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public String getName() {
        return name;
    }
}
