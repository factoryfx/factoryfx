package io.github.factoryfx.factory.storage.migration.datamigration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathBuilder<V>{
    private final List<AttributePathElement> path= new ArrayList<>();

    public PathBuilder() {

    }

    public static <V> PathBuilder<V> value() {
        return new PathBuilder<>();
    }

    public PathBuilder<V> pathElement(String pathElement) {
        path.add(new RefAttributePathElement(pathElement));
        return this;
    }

    public PathBuilder<V> pathElement(String pathElement, int index) {
        path.add(new RefListAttributePathElement(pathElement,index));
        return this;
    }

    public AttributePathTarget<V> attribute(String attribute) {
        return new AttributePathTarget<>(path,attribute,-1);
    }

    public AttributePathTarget<V> attribute(String attribute, int index) {
        return new AttributePathTarget<>(path,attribute,index);
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
     * @param <V> attribute type
     * @return path
     */
    public static <V> AttributePathTarget<V> of(String path) {
        Pattern pattern = Pattern.compile("([[a-zA-Z_$]]*)\\[(\\d+)]");
        List<AttributePathElement> pathList= new ArrayList<>();

        String[] split = path.split("\\.");
        String attributeName= split[split.length-1];

        int attributeNameIndex=-1;
        Matcher attributeIndexMatcher = pattern.matcher(attributeName);
        if (attributeIndexMatcher.matches()){
            attributeName=attributeIndexMatcher.group(1);
            attributeNameIndex=Integer.parseInt(attributeIndexMatcher.group(2));
        }

        for (String pathElement : Arrays.asList(split).subList(0, split.length-1)) {
            Matcher matcher = pattern.matcher(pathElement);
            if (matcher.matches()){
                pathList.add(new RefListAttributePathElement(matcher.group(1),Integer.parseInt(matcher.group(2))));
            } else {
                pathList.add(new RefAttributePathElement(pathElement));
            }
        }
        return new AttributePathTarget<>(pathList,attributeName,attributeNameIndex);
    }

//    public static <V> AttributePathTarget<V> ofList(Class<?> valueClass, String path) {
//        Pattern pattern = Pattern.compile("([[a-zA-Z_$]]*)\\[(\\d+)]");
//        List<AttributePathElement> pathList= new ArrayList<>();
//
//        String[] split = path.split("\\.");
//        String attributeName= split[split.length-1];
//
//        int attributeNameIndex=0;
//        Matcher attributeIndexMatcher = pattern.matcher(attributeName);
//        if (attributeIndexMatcher.matches()){
//            attributeName=attributeIndexMatcher.group(1);
//            attributeNameIndex=Integer.parseInt(attributeIndexMatcher.group(2));
//        }
//
//        for (String pathElement : Arrays.asList(split).subList(0, split.length-1)) {
//            Matcher matcher = pattern.matcher(pathElement);
//            if (matcher.matches()){
//                pathList.add(new RefListAttributePathElement(matcher.group(1),Integer.parseInt(matcher.group(2))));
//            } else {
//                pathList.add(new RefAttributePathElement(pathElement));
//            }
//        }
//        return new AttributePathTarget<V>(pathList,attributeName,attributeNameIndex,valueClass);
//    }

}
