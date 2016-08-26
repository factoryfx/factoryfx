package de.factoryfx.adminui.angularjs.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.reflect.ClassPath;
import de.factoryfx.factory.FactoryBase;

public class ClasspathBasedFactoryProvider {

    /**
     * @param basePackage base package for the factory to avoid scanning the whole classpath
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Class<? extends FactoryBase>> get(String basePackage) {
        List<Class<? extends FactoryBase>> result = new ArrayList<>();
        try {
            for (ClassPath.ClassInfo classInfo : ClassPath.from(ClasspathBasedFactoryProvider.class.getClassLoader()).getAllClasses()) {
                if (classInfo.getName().startsWith(basePackage)) {
                    Class<?> clazz = classInfo.load();
                    if (FactoryBase.class.isAssignableFrom(clazz) && clazz != FactoryBase.class) {
                        result.add((Class<FactoryBase>) clazz);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public List<Class<? extends FactoryBase>> get(Package packageParam){
        return get(packageParam.getName());
    }

    /**
     *
     * @param root class in the root package for all Factories. {@link ClasspathBasedFactoryProvider#get(String)}
     * @return
     */
    public List<Class<? extends FactoryBase>> get(Class<? extends FactoryBase> root){
        return get(root.getPackage().getName());
    }
}
