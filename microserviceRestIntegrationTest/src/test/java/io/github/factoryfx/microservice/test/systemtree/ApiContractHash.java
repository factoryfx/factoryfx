package io.github.factoryfx.microservice.test.systemtree;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.stream.Collectors;

/**
 * stable reflection hash over a JAX-RS api interface: method names, parameter/return types and jax-rs annotations.
 * provider and consumer compute it from the shared api jar; a mismatch at deploy time means jar-version skew.
 */
public class ApiContractHash {

    public static String hashOf(Class<?> api) {
        String description = Arrays.stream(api.getMethods())
                .map(ApiContractHash::describeMethod)
                .sorted()
                .collect(Collectors.joining("\n", api.getName() + "\n", ""));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(description.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String describeMethod(Method method) {
        String parameters = Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining(","));
        String annotations = Arrays.stream(method.getAnnotations())
                .map(ApiContractHash::describeAnnotation)
                .sorted()
                .collect(Collectors.joining(","));
        return method.getName() + "(" + parameters + "):" + method.getReturnType().getName() + "[" + annotations + "]";
    }

    private static String describeAnnotation(Annotation annotation) {
        if (annotation instanceof jakarta.ws.rs.Path path) {
            return "Path=" + path.value();
        }
        if (annotation instanceof jakarta.ws.rs.Produces produces) {
            return "Produces=" + String.join("|", produces.value());
        }
        if (annotation instanceof jakarta.ws.rs.Consumes consumes) {
            return "Consumes=" + String.join("|", consumes.value());
        }
        return annotation.annotationType().getName();
    }
}
