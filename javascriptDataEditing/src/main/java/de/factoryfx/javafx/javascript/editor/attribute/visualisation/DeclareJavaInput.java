package de.factoryfx.javafx.javascript.editor.attribute.visualisation;

import com.google.javascript.jscomp.parsing.parser.Keywords;
import de.factoryfx.javascript.data.attributes.types.JSDoc;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeclareJavaInput {

    private HashSet<Class<?>> classesToBeDeclared = new HashSet<>();
    private HashMap<String,Class<?>> variablesToBeDeclared = new HashMap<>();
    private Immutables immutables = new Immutables();

    public DeclareJavaInput declareClasses(Class<?> ... clazz) {
        Stream.of(clazz).forEach(this::declareClass);
        return this;
    }

    public DeclareJavaInput declareClass(Class<?> clazz) {
        classesToBeDeclared.add(clazz);
        return this;
    }

    public DeclareJavaInput declareVariable(String name, Class<?> type) {
        variablesToBeDeclared.put(name,type);
        classesToBeDeclared.add(type);
        return this;
    }


    public String sourceScript() {
        StringBuilder source = new StringBuilder();
        HashSet<Class<?>> classesAlreadyDeclared = new HashSet<>();
        classesToBeDeclared.forEach(c->{
            if (!classesAlreadyDeclared.contains(c)) {
                declareClass(classesAlreadyDeclared,c,source);
            }
        });
        variablesToBeDeclared.forEach((name,type)->{
            source.append("/** @constant @readonly @type {!").append(toJsType(type)).append("} */\n");
            source.append("var ").append(name).append(";\n");//append(" = ").append(constructTypeInstance(type)).append("\n");
        });
        return source.toString();
    }

    private void declareClass(HashSet<Class<?>> classesAlreadyDeclared, Class<?> clazz, StringBuilder source) {
        classesAlreadyDeclared.add(clazz);
        String className = clazz.getSimpleName();
        String declProto = className+".prototype.";
        String immutable = immutables.contains(clazz)?" * @nosideeffects\n":"";

        source.append("/**\n * @constructor\n * @struct\n").append(immutable);
        if (classesToBeDeclared.contains(clazz.getSuperclass())) {
            source.append(" * @extends ").append(clazz.getSuperclass().getSimpleName()).append("\n");
        }
        Stream.of(clazz.getInterfaces()).forEach(c->{
            if (classesToBeDeclared.contains(c)) {
                source.append(" * @extends ").append(c.getSimpleName()).append("\n");
            }
        });
        source.append(" */\nfunction "+ className +"() {}\n");
        HashSet<Class<?>> reachableClasses = new HashSet<>();
        for (Field f : clazz.getFields()) {
            if (f.getDeclaringClass() == Object.class || !allowTypeReference(f.getType()))
                continue;
            Class<?> closestType = getClosestType(f.getType()).orElse(f.getType());
            reachableClasses.add(closestType);
            int modifiers = f.getModifiers();
            boolean isNullable = f.isAnnotationPresent(Nullable.class);
            if (Modifier.isStatic(modifiers))
                continue;
            if (!Modifier.isPublic(modifiers))
                continue;
            if (Stream.of(Keywords.values()).anyMatch(kw->kw.value.equals(f.getName())))
                continue;
            source.append("/** ");
            JSDoc jsDoc = f.getAnnotation(JSDoc.class);
            if (jsDoc == null) {
                boolean isConstant = Modifier.isFinal(modifiers) || closestType != f.getType();
                if (isConstant)
                    source.append("@readonly @constant {");
                else {
                    source.append("@type {");
                }
                if (!isNullable)
                    source.append("!");
                source.append(toJsType(closestType)).append("}");
            } else {
                source.append(jsDoc.value());
            }
            source.append(" */\n").append(declProto).append(f.getName());
                    //.append(" = ").append(constructTypeInstance(f.getType()));
            source.append(";\n");
        }
        Type t = clazz.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)t;
            for (Type at : pt.getActualTypeArguments()) {
                if (at instanceof Class) {
                    reachableClasses.add((Class)at);
                }
            }
        }
        List<Method> methods = Stream.of(clazz.getMethods()).filter(m->Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
        for (String methodName : methods.stream().map(m->m.getName()).distinct().collect(Collectors.toList())) {
            List<Method> matchingMethods = methods.stream()
                    .filter(m->m.getName().equals(methodName) &&
                            shouldDeclareMethod(m)).collect(Collectors.toList());
            if (matchingMethods.isEmpty())
                continue;
            boolean ellipsis = matchingMethods.size() > 1;
            Function<Method,String> declareReturn = mthd -> {
                StringBuilder ret = new StringBuilder();
                if (mthd.getReturnType() != void.class && mthd.getReturnType() != char.class) {
                    ret.append(" * @suppress {externsValidation}\n");
                    ret.append(" * @return {");
                    boolean allowsNullableReturn = mthd.getAnnotatedReturnType() != null && mthd.getAnnotatedReturnType().isAnnotationPresent(Nullable.class);
                    if (!allowsNullableReturn)
                        ret.append("!");
                    Class<?> closestType = getClosestType(mthd.getReturnType()).orElse(mthd.getReturnType());
                    reachableClasses.add(closestType);
                    ret.append(toJsType(closestType));
                    ret.append("}\n");
                } else {
                    ret.append(" * @return {undefined}\n");
                }
                ret.append(immutable);
                return ret.toString();
            };
            Method method = matchingMethods.get(0);
            JSDoc jsDoc = method.getAnnotation(JSDoc.class);
            if (!ellipsis) {
                source.append("/**\n");
                if (jsDoc != null)
                    source.append(" * "+jsDoc.value()+"\n");
                else {
                    source.append(declareReturn.apply(method));
                }
                source.append(" */\n");
                source.append(declProto).append(methodName).append(" = function(");
                if (method.getParameters().length > 0) {
                    for (Parameter parameter : method.getParameters()) {
                        boolean isNullable = parameter.isAnnotationPresent(Nullable.class);
                        JSDoc jsParam = parameter.getAnnotation(JSDoc.class);
                        if (jsParam != null) {
                            source.append(" /** @type "+jsParam.value()+" */ ");
                        } else {
                            Class<?> closestType = getClosestType(parameter.getType()).orElse(parameter.getType());
                            reachableClasses.add(closestType);
                            source.append(" /** @type {");
                            if (!isNullable) {
                                source.append("!");
                            }
                            source.append(toJsType(closestType)).append("} */ ");
                        }
                        String parameterName = "_"+parameter.getName();
                        source.append(parameterName).append(",");
                    }
                    source.setLength(source.length() - 1);
                }
                source.append(") { ");
                /*
                if (!(void.class == method.getReturnType())) {
                    source.append("return ").append(constructTypeInstance(method.getReturnType())).append("; ");
                    reachableClasses.add(method.getReturnType());
                }
                */
                source.append("};\n");
            } else {
                source.append("/**\n");
                if (jsDoc != null)
                    source.append(" * "+jsDoc.value()+"\n");
                else {
                    source.append(" * @param {...*} params\n");
                }
                source.append(" */\n");
                source.append(declProto).append(methodName).append(" = function(params) { }\n");
            }
        }
        source.append("\n");
        reachableClasses.removeAll(classesAlreadyDeclared);
        reachableClasses.removeIf(this::isBuiltIn);
        reachableClasses.removeIf(c->c.isArray());
        reachableClasses.removeIf(c->c.isPrimitive());
        reachableClasses.forEach(r->{
            if (!classesAlreadyDeclared.contains(r)) {
                declareClass(classesAlreadyDeclared, r, source);
            }
        });
    }

    private boolean shouldDeclareMethod(Method m) {
        return allowTypeReference(m.getDeclaringClass())
               && (m.getAnnotation(JSDoc.class) != null || (allowTypeReference(m.getReturnType())
               && Stream.of(m.getParameters()).allMatch(this::allowParameter)
               && Stream.of(Keywords.values()).noneMatch(kw->kw.value.equals(m.getName()))));
    }

    private boolean allowParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(JSDoc.class) || allowTypeReference(parameter.getType());
    }

    private boolean allowTypeReference(Class<?> type) {
        return !jsNameClash(type.getSimpleName())
                && (isNumber(type) || CharSequence.class.isAssignableFrom(type) || classesToBeDeclared.stream().anyMatch(c->c.isAssignableFrom(type)) || void.class == type || isBoolean(type));
    }

    private Optional<Class<?>> getClosestType(Class<?> type) {
        while (type != Object.class && type != null) {
            if (classesToBeDeclared.contains(type))
                return Optional.of(type);
            for (Class<?> iface : Optional.ofNullable(type.getInterfaces()).orElse(new Class[0])) {
                if (classesToBeDeclared.contains(iface))
                    return Optional.of(iface);
            }
            type = type.getSuperclass();
        }
        return Optional.empty();
    }

    static final List<Class> primitiveNumbers = Arrays.asList(float.class,double.class,byte.class,short.class,int.class,long.class);
    private String toJsType(Class<?> type) {
        if (isNumber(type))
            return "number";
        if (isBoolean(type))
            return "boolean";
        if (CharSequence.class.isAssignableFrom(type))
            return "string";
        if (void.class == type)
            return "undefined";
        return type.getSimpleName();
    }

    private boolean isBuiltIn(Class<?> clazz) {
        if (clazz == null || clazz.getPackage() == null)
            return true;

        return isNumber(clazz) || clazz.getPackage().getName().startsWith("java");
    }

    private boolean isNumber(Class<?> type) {
        return primitiveNumbers.contains(type) || Number.class.isAssignableFrom(type);
    }

    private boolean isBoolean(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }

    private boolean jsNameClash(String name) {
        return Arrays.asList("Iterator").contains(name);
    }

    public String jsDocAnnotationFor(Class<?> aClass) {
        return "/** @type {!"+toJsType(aClass)+"} */";
    }
}
