package io.github.factoryfx.factory.typescript.generator.construct;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListBaseAttribute;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.typescript.generator.construct.atttributes.AttributeToTsMapperManager;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.*;
import java.util.stream.Collectors;

public class DataGeneratedTs<R extends FactoryBase<?,R>, L,  F extends FactoryBase<L,R>> {

    private final Class<F> clazz;
    private final TsFile dataTsClass;
    private final TsFile dataCreatorTsClass;
    private final TsFile attributeMetadataTsClass;
    private final Map<Class<? extends FactoryBase<?,R>>,TsClassConstructed> dataToOverrideTs;
    private final TsFile attributeAccessorClass;
    private final AttributeToTsMapperManager attributeToTsMapperManager;
    private final TsFile attributeTypeEnumTsEnum;
    private final TsFile dynamicDataDictionaryTsClass;

    public DataGeneratedTs(Class<F> clazz, Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToOverrideTs, TsFile dataTsClass, TsFile dynamicDataDictionaryTsClass , TsFile dataCreatorTsClass, TsFile attributeTsClass, TsFile attributeAccessorClass, AttributeToTsMapperManager attributeToTsMapperManager, TsFile attributeTypeEnumTsEnum) {
        this.clazz = clazz;
        this.dataTsClass = dataTsClass;
        this.dataToOverrideTs = dataToOverrideTs;
        this.dataCreatorTsClass = dataCreatorTsClass;
        this.attributeMetadataTsClass = attributeTsClass;
        this.attributeAccessorClass = attributeAccessorClass;
        this.attributeToTsMapperManager = attributeToTsMapperManager;
        this.attributeTypeEnumTsEnum = attributeTypeEnumTsEnum;
        this.dynamicDataDictionaryTsClass= dynamicDataDictionaryTsClass;
    }

    public TsFile complete(TsClassConstructed tsClass){
        ArrayList<TsAttribute> attributes = new ArrayList<>();
        FactoryMetadata<R, F> factoryMetadata = FactoryMetadataManager.getMetadata(clazz);



        factoryMetadata.visitAttributeMetadata((metadata) -> {
            if (attributeToTsMapperManager.isMappable(metadata.attributeClass)){
                attributes.add(getTsAttribute(metadata));
            }
        });

        factoryMetadata.visitAttributeMetadata((metadata) -> {
            if (attributeToTsMapperManager.isMappable(metadata.attributeClass)) {
                attributes.add(getTsAttributeMetadata(metadata));
            }
        });





        ArrayList<TsMethod> methods = new ArrayList<>();

        factoryMetadata.visitAttributeMetadata((metadata) -> {
            if (attributeToTsMapperManager.isMappable(metadata.attributeClass)) {
                methods.add(getTsAttributeAccessor(metadata, tsClass));
            }
        });


        methods.add(createMapValuesFromJson(factoryMetadata));
        methods.add(createAddMapValueToJson(factoryMetadata));
        methods.add(createCollectChildren(factoryMetadata));
        methods.add(createListAttributeAccessor(factoryMetadata,tsClass));
        methods.add(createCreateNewChildFactory());


        tsClass.parent=dataTsClass;
        tsClass.attributes=attributes;
        tsClass.methods=methods;

        tsClass.abstractClass();

        tsClass.constructor= new TsConstructor(List.of(),new TsMethodCode("super();\nthis.javaClass=\""+clazz.getName()+"\";\nthis.id=this.uuidV4();"));

        return tsClass;
    }


    private TsMethod createListAttributeAccessor(FactoryMetadata<R, F> factoryMetadata, TsClassConstructed tsClass) {
        StringBuilder code=new StringBuilder("let result: AttributeAccessor<any>[]=[];\n");
        factoryMetadata.visitAttributeMetadata((metadata) -> {
            if (attributeToTsMapperManager.isMappable(metadata.attributeClass)){
                code.append("result.push(this.").append(metadata.attributeVariableName).append("Accessor());\n");
            }
        });
        code.append("return result;");
        return new TsMethod("listAttributeAccessor", List.of(),
                new TsMethodResult(new TsTypeArray(new TsTypeClass(attributeAccessorClass,new TsTypePrimitive("any")))),new TsMethodCode(code.toString(), Set.of()),"public");
    }



    private TsMethod createCreateNewChildFactory() {
        Set<TsFile> mapValuesFromJsonImports = new HashSet<>();

        String fromJsonCode =
                "let data = new DataCreator().createData(json, {}, this);\n" +
                "if (data){\n" +
                "    return data;\n" +
                "} else {\n" +
                "    throw new Error('json parameter was snull');\n" +
                "}\n";
        return new TsMethod("createNewChildFactory",
                List.of(new TsMethodParameter("json",new TsTypePrimitive("any"))),
                new TsMethodResult(new TsTypeClass(dataTsClass)),new TsMethodCode(fromJsonCode, mapValuesFromJsonImports),"public");
    }

    private TsMethod createCollectChildren(FactoryMetadata<R, F> factoryMetadata) {
        StringBuilder fromJsonCode=new StringBuilder();
        fromJsonCode.append("let result: Array<Data>=[];\n");
        Set<TsFile> mapValuesFromJsonImports = new HashSet<>();
        factoryMetadata.visitAttributeMetadata((metadata) -> {
            if (FactoryBaseAttribute.class.isAssignableFrom(metadata.attributeClass)){
                Class<? extends FactoryBase<?,?>> referenceClass = metadata.referenceClass;
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("if (this.").append(metadata.attributeVariableName).append(") result.push(this.").append(metadata.attributeVariableName).append(");\n");
            }
            if (FactoryListBaseAttribute.class.isAssignableFrom(metadata.attributeClass)){
                Class<? extends FactoryBase<?,?>> referenceClass = metadata.referenceClass;
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("if (this.").append(metadata.attributeVariableName).append(") for (let child of this.").append(metadata.attributeVariableName).append(") {result.push(child)};\n");
            }
        });
        fromJsonCode.append("return result;");

        return new TsMethod("collectChildrenFlat",
                List.of(),
                new TsMethodResult(new TsTypeArray(new TsTypeClass(dataTsClass))),new TsMethodCode(fromJsonCode.toString(), mapValuesFromJsonImports),"protected");
    }

    private TsMethod createMapValuesFromJson(FactoryMetadata<R, F> factoryMetadata) {
        StringBuilder fromJsonCode=new StringBuilder();
        Set<TsFile> jsonImports = new HashSet<>();
        factoryMetadata.visitAttributeMetadata((metadata) -> {
            if (attributeToTsMapperManager.isMappable(metadata.attributeClass)){
                fromJsonCode.append(attributeToTsMapperManager.getMapFromJsonExpression(metadata,jsonImports));
            }
        });

        return new TsMethod("mapValuesFromJson",
                List.of(new TsMethodParameter("json",new TsTypePrimitive("any")),new TsMethodParameter("idToDataMap",new TsTypePrimitive("any")), new TsMethodParameter("dataCreator",new TsTypeClass(dataCreatorTsClass)), new TsMethodParameter("dynamicDataDictionary",new TsTypeClass(dynamicDataDictionaryTsClass))),
                new TsMethodResultVoid(),new TsMethodCode(fromJsonCode.toString(), jsonImports),"protected");
    }

    private TsMethod createAddMapValueToJson(FactoryMetadata<R, F> factoryMetadata) {
        StringBuilder toJsonCode=new StringBuilder();
        Set<TsFile> jsonImports = new HashSet<>();
        factoryMetadata.visitAttributeMetadata((metadata) -> {
            if (attributeToTsMapperManager.isMappable(metadata.attributeClass)){
                toJsonCode.append(attributeToTsMapperManager.getMapToJsonExpression(metadata,jsonImports));
            }
        });
        return new TsMethod("mapValuesToJson",
                List.of(new TsMethodParameter("idToDataMap",new TsTypePrimitive("any")),new TsMethodParameter("result",new TsTypePrimitive("any"))),
                new TsMethodResultVoid(),new TsMethodCode(toJsonCode.toString(),jsonImports),"protected");
    }

    private TsAttribute getTsAttribute(AttributeMetadata metadata){
        TsType tsType = getTsType(metadata);
        if (tsType instanceof TsTypeArray) {
            return new TsAttribute(metadata.attributeVariableName, tsType,false,true,false,List.of());
        }
        return new TsAttribute(metadata.attributeVariableName, tsType);
    }

    private TsMethod getTsAttributeAccessor(AttributeMetadata metadata, TsClassConstructed tsClassName) {
        TsType attributeTsType = getTsType(metadata);
        String createCode="return new AttributeAccessor<"+ attributeTsType.construct()+">("+tsClassName.getName()+"."+metadata.attributeVariableName+"Metadata,this,\""+metadata.attributeVariableName+"\");";
        return new TsMethod(metadata.attributeVariableName+"Accessor",
                List.of(),
                new TsMethodResult(new TsTypeClass(attributeAccessorClass, attributeTsType)),new TsMethodCode(createCode),"public");
    }

    private TsAttribute getTsAttributeMetadata(AttributeMetadata metadata){
        ArrayList<TsValue> constructorParameters = new ArrayList<>();
        constructorParameters.add(new TsValueString(metadata.labelText.internal_getPreferred(Locale.ENGLISH)));
        constructorParameters.add(new TsValueString(metadata.labelText.internal_getPreferred(Locale.GERMAN)));
        constructorParameters.add(new TsValueEnum(attributeToTsMapperManager.getAttributeTypeValue(metadata),attributeTypeEnumTsEnum));
        constructorParameters.add(new TsValueBoolean(!metadata.required));
        constructorParameters.add(new TsValueStringArray(getPossibleEnumValues(metadata)));

        return new TsAttribute(metadata.attributeVariableName+"Metadata", new TsTypeClass(attributeMetadataTsClass,getTsType(metadata)),true,true,true, constructorParameters);
    }

    private TsType getTsType(AttributeMetadata metadata){
        return attributeToTsMapperManager.getTsType(metadata);
    }

    private List<String> getPossibleEnumValues(AttributeMetadata metadata) {
        if (metadata.enumClass!=null ){
            return Arrays.stream(metadata.enumClass.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }
}
