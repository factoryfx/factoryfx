package io.github.factoryfx.factory.typescript.generator.construct;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.ReferenceBaseAttribute;
import io.github.factoryfx.factory.attribute.types.EnumAttribute;
import io.github.factoryfx.factory.attribute.types.EnumListAttribute;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.typescript.generator.construct.atttributes.AttributeToTsMapperManager;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.*;
import java.util.stream.Collectors;

public class DataGeneratedTs<R extends FactoryBase<?,R>, L,  F extends FactoryBase<L,R>> {

    private final Class<F> clazz;
    private final TsFile dataTsClass;
    private final TsFile staticAttributeValueAccessorTsClass;
    private final TsFile dataCreatorTsClass;
    private final TsFile attributeMetadataTsClass;
    private final Map<Class<? extends FactoryBase<?,R>>,TsClassConstructed> dataToOverrideTs;
    private final TsFile attributeAccessorClass;
    private final AttributeToTsMapperManager attributeToTsMapperManager;
    private final TsEnumConstructed attributeTypeEnumTsEnum;
    private final TsFile dynamicDataDictionaryTsClass;

    public DataGeneratedTs(Class<F> clazz, Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToOverrideTs, TsFile dataTsClass, TsFile dynamicDataDictionaryTsClass, TsFile staticAttributeValueAccessorTsClass , TsFile dataCreatorTsClass, TsFile attributeTsClass, TsFile attributeAccessorClass, AttributeToTsMapperManager attributeToTsMapperManager, TsEnumConstructed attributeTypeEnumTsEnum) {
        this.clazz = clazz;
        this.dataTsClass = dataTsClass;
        this.dataToOverrideTs = dataToOverrideTs;
        this.dataCreatorTsClass = dataCreatorTsClass;
        this.attributeMetadataTsClass = attributeTsClass;
        this.attributeAccessorClass = attributeAccessorClass;
        this.attributeToTsMapperManager = attributeToTsMapperManager;
        this.attributeTypeEnumTsEnum = attributeTypeEnumTsEnum;
        this.staticAttributeValueAccessorTsClass = staticAttributeValueAccessorTsClass;
        this.dynamicDataDictionaryTsClass= dynamicDataDictionaryTsClass;
    }

    public TsFile complete(TsClassConstructed tsClass){
        ArrayList<TsAttribute> attributes = new ArrayList<>();
        F data = FactoryMetadataManager.getMetadata(clazz).newInstance();
        FactoryMetadataManager.getMetadata(clazz).setAttributeReferenceClasses(data);

        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeToTsMapperManager.isMappable(attribute.getClass())){
                attributes.add(getTsAttribute(attributeVariableName,attribute));
            }
        });

        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeToTsMapperManager.isMappable(attribute.getClass())) {
                attributes.add(getTsAttributeMetadata(attributeVariableName, attribute));
            }
        });




        ArrayList<TsMethod> methods = new ArrayList<>();

        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeToTsMapperManager.isMappable(attribute.getClass())) {
                methods.add(getTsAttributeAccessor(attributeVariableName, attribute, tsClass));
            }
        });


        methods.add(createMapValuesFromJson(data));
        methods.add(createAddMapValueToJson(data));
        methods.add(createCollectChildren(data));
        methods.add(createListAttributeAccessor(data,tsClass));

        tsClass.parent=dataTsClass;
        tsClass.attributes=attributes;
        tsClass.methods=methods;

        tsClass.abstractClass();

        return tsClass;
    }


    private TsMethod createListAttributeAccessor(FactoryBase<?,?> data, TsClassConstructed tsClass) {
        StringBuilder code=new StringBuilder("let result: AttributeAccessor<any>[]=[];\n");
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeToTsMapperManager.isMappable(attribute.getClass())){
                code.append("result.push(this.").append(attributeVariableName).append("Accessor());\n");
            }
        });
        code.append("return result;");
        return new TsMethod("listAttributeAccessor", List.of(),
                new TsMethodResult(new TsTypeArray(new TsTypeClass(attributeAccessorClass,new TsTypePrimitive("any")))),new TsMethodCode(code.toString(), Set.of()),"public");
    }

    private TsMethod createCollectChildren(FactoryBase<?,?> data) {
        StringBuilder fromJsonCode=new StringBuilder();
        fromJsonCode.append("let result: Array<Data>=[];\n");
        Set<TsFile> mapValuesFromJsonImports = new HashSet<>();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attribute instanceof FactoryBaseAttribute){
                Class referenceClass = ((FactoryBaseAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("if (this.").append(attributeVariableName).append(") result.push(this.").append(attributeVariableName).append(");\n");
                return;
            }
            if (attribute instanceof FactoryListBaseAttribute){
                Class referenceClass = ((FactoryListBaseAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("if (this.").append(attributeVariableName).append(") for (let child of this.").append(attributeVariableName).append(") {result.push(child)};\n");
                return;
            }
        });
        fromJsonCode.append("return result;");

        return new TsMethod("collectChildrenFlat",
                List.of(),
                new TsMethodResult(new TsTypeArray(new TsTypeClass(dataTsClass))),new TsMethodCode(fromJsonCode.toString(), mapValuesFromJsonImports),"protected");
    }

    private TsMethod createMapValuesFromJson(FactoryBase<?,?> data) {
        StringBuilder fromJsonCode=new StringBuilder();
        Set<TsFile> jsonImports = new HashSet<>();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeToTsMapperManager.isMappable(attribute.getClass())){
                fromJsonCode.append(attributeToTsMapperManager.getMapFromJsonExpression(attributeVariableName,attribute,jsonImports));
            }
        });

        return new TsMethod("mapValuesFromJson",
                List.of(new TsMethodParameter("json",new TsTypePrimitive("any")),new TsMethodParameter("idToDataMap",new TsTypePrimitive("any")), new TsMethodParameter("dataCreator",new TsTypeClass(dataCreatorTsClass)), new TsMethodParameter("dynamicDataDictionary",new TsTypeClass(dynamicDataDictionaryTsClass))),
                new TsMethodResultVoid(),new TsMethodCode(fromJsonCode.toString(), jsonImports),"protected");
    }

    private TsMethod createAddMapValueToJson(FactoryBase<?,?> data) {
        StringBuilder toJsonCode=new StringBuilder();
        Set<TsFile> jsonImports = new HashSet<>();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeToTsMapperManager.isMappable(attribute.getClass())){
                toJsonCode.append(attributeToTsMapperManager.getMapToJsonExpression(attributeVariableName,attribute,jsonImports));
            }
        });
        return new TsMethod("mapValuesToJson",
                List.of(new TsMethodParameter("idToDataMap",new TsTypePrimitive("any")),new TsMethodParameter("result",new TsTypePrimitive("any"))),
                new TsMethodResultVoid(),new TsMethodCode(toJsonCode.toString(),jsonImports),"protected");
    }

    private TsAttribute getTsAttribute(String attributeVariableName, Attribute<?, ?> attribute){
        return new TsAttribute(attributeVariableName,getTsType(attribute));
    }

    private TsMethod getTsAttributeAccessor(String attributeVariableName, Attribute<?, ?> attribute, TsClassConstructed tsClassName) {
        TsType attributeTsType = getTsType(attribute);
        String createCode="return new AttributeAccessor<"+ attributeTsType.construct()+">("+tsClassName.getName()+"."+attributeVariableName+"Metadata,new StaticAttributeValueAccessor<"+ attributeTsType.construct()+">(this,\""+attributeVariableName+"\"),\""+attributeVariableName+"\");";
        return new TsMethod(attributeVariableName+"Accessor",
                List.of(),
                new TsMethodResult(new TsTypeClass(attributeAccessorClass, attributeTsType)),new TsMethodCode(createCode,Set.of(staticAttributeValueAccessorTsClass)),"public");
    }

    private TsAttribute getTsAttributeMetadata(String attributeVariableName, Attribute<?, ?> attribute){
        ArrayList<TsValue> constructorParameters = new ArrayList<>();
        constructorParameters.add(new TsValueString(attribute.internal_getPreferredLabelText(Locale.ENGLISH)));
        constructorParameters.add(new TsValueString(attribute.internal_getPreferredLabelText(Locale.GERMAN)));
        constructorParameters.add(new TsValueEnum(attributeToTsMapperManager.getAttributeTypeValue(attribute),attributeTypeEnumTsEnum));
        constructorParameters.add(new TsValueBoolean(!attribute.internal_required()));
        constructorParameters.add(new TsValueStringArray(getPossibleEnumValues(attribute)));

        return new TsAttribute(attributeVariableName+"Metadata", new TsTypeClass(attributeMetadataTsClass,getTsType(attribute)),true,true,true, constructorParameters);
    }

    private TsType getTsType(Attribute<?, ?> attribute){
        return attributeToTsMapperManager.getTsType(attribute);
    }

    private List<String> getPossibleEnumValues(Attribute<?, ?> attribute) {
        if (attribute instanceof EnumAttribute){
            return ((EnumAttribute<?>)attribute).internal_possibleEnumValues().stream().map(Enum::toString).collect(Collectors.toList());
        }
        if (attribute instanceof EnumListAttribute){
            return ((EnumListAttribute<?>)attribute).internal_possibleEnumValues().stream().map(Enum::toString).collect(Collectors.toList());
        }
        return List.of();
    }
}
