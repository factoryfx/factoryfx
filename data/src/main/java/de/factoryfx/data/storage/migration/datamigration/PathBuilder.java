package de.factoryfx.data.storage.migration.datamigration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathBuilder<V>{
    private Class<V> valueClass;
    private final List<AttributePathElement> path= new ArrayList<>();

    public PathBuilder(Class<V> valueClass) {
        this.valueClass = valueClass;
    }

    public static <V> PathBuilder<V> value(Class<V> valueClass) {
        return new PathBuilder<>(valueClass);
    }

    public PathBuilder<V> pathElement(String pathElement) {
        path.add(new RefAttributePathElement(pathElement));
        return this;
    }

    public PathBuilder<V> pathElement(String pathElement, int index) {
        path.add(new RefListAttributePathElement(pathElement,index));
        return this;
    }

    public AttributePath<V> attribute(String attribute) {
        return new AttributePath<>(valueClass,path,attribute);
    }

    /**
     * the entire path based string<br>
     * examples:
     * <ul>
     * <li>referenceAttribute.stringAttribute</li>
     * <li>referenceListAttribute[123].stringAttribute</li>
     * <li>referenceAttribute.referenceListAttribute[123].stringAttribute</li>
     * </ul>
     *
     * @param path path as string
     * @return
     */
    public AttributePath<V> of(String path) {
        List<AttributePathElement> pathList= new ArrayList<>();

        String[] split = path.split("\\.");
        String attributeName= split[split.length-1];
        for (String pathElement : Arrays.asList(split).subList(0, split.length-1)) {

            Pattern pattern = Pattern.compile("([[a-zA-Z_$]]*)\\[(\\d+)\\]");
            Matcher matcher = pattern.matcher(pathElement);
            if (matcher.matches()){
                pathList.add(new RefListAttributePathElement(matcher.group(1),Integer.parseInt(matcher.group(2))));
            } else {
                pathList.add(new RefAttributePathElement(pathElement));
            }
        }
        return new AttributePath<>(valueClass,pathList,attributeName);
    }

}
