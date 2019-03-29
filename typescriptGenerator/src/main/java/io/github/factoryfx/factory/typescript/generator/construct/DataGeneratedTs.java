package io.github.factoryfx.factory.typescript.generator.construct;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceBaseAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListBaseAttribute;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;
import io.github.factoryfx.factory.typescript.generator.construct.atttributes.AttributeToTsMapperManager;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.*;

public class DataGeneratedTs<R extends FactoryBase<?,R>> {

    private final Class<? extends FactoryBase<?,R>> clazz;
    private final TsFile dataTsClass;
    private final TsFile dataCreatorTsClass;
    private final TsFile attributeMetadataTsClass;
    private final Map<Class<? extends FactoryBase<?,R>>,TsClassConstructed> dataToOverrideTs;
    private final TsFile attributeAccessorClass;
    private final AttributeToTsMapperManager attributeToTsMapperManager;
    private final TsEnumConstructed attributeTypeEnumTsEnum;

    public DataGeneratedTs(Class<? extends FactoryBase<?,R>> clazz, Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToOverrideTs, TsFile dataTsClass, TsFile dataCreatorTsClass, TsFile attributeTsClass, TsFile attributeAccessorClass, AttributeToTsMapperManager attributeToTsMapperManager, TsEnumConstructed attributeTypeEnumTsEnum) {
        this.clazz = clazz;
        this.dataTsClass = dataTsClass;
        this.dataToOverrideTs = dataToOverrideTs;
        this.dataCreatorTsClass = dataCreatorTsClass;
        this.attributeMetadataTsClass = attributeTsClass;
        this.attributeAccessorClass = attributeAccessorClass;
        this.attributeToTsMapperManager = attributeToTsMapperManager;
        this.attributeTypeEnumTsEnum = attributeTypeEnumTsEnum;
    }

    public TsFile complete(TsClassConstructed tsClass){
        ArrayList<TsAttribute> attributes = new ArrayList<>();
        FactoryBase<?,R> data = FactoryMetadataManager.getMetadata(clazz).newInstance();
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
        StringBuilder code=new StringBuilder("let result: AttributeAccessor<any,"+tsClass.getName()+">[]=[];\n");
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attributeToTsMapperManager.isMappable(attribute.getClass())){
                code.append("result.push(this.").append(attributeVariableName).append("Accessor());\n");
            }
        });
        code.append("return result;");
        return new TsMethod("listAttributeAccessor", List.of(),
                new TsMethodResult(new TsTypeArray(new TsTypeClass(attributeAccessorClass,new TsTypePrimitive("any"),new TsTypeClass(tsClass)))),new TsMethodCode(code.toString(), Set.of()),"protected");
    }

    private TsMethod createCollectChildren(FactoryBase<?,?> data) {
        StringBuilder fromJsonCode=new StringBuilder();
        Set<TsFile> mapValuesFromJsonImports = new HashSet<>();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attribute instanceof FactoryReferenceBaseAttribute){
                Class referenceClass = ((FactoryReferenceBaseAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("this.collectDataChildren(this.").append(attributeVariableName).append(",idToDataMap);\n");
                return;
            }
            if (attribute instanceof FactoryReferenceListBaseAttribute){
                Class referenceClass = ((FactoryReferenceListBaseAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("this.collectDataArrayChildren(this.").append(attributeVariableName).append(",idToDataMap);\n");
                return;
            }
        });

        return new TsMethod("collectChildrenRecursiveIntern",
                List.of(new TsMethodParameter("idToDataMap",new TsTypePrimitive("any"))),
                new TsMethodResultVoid(),new TsMethodCode(fromJsonCode.toString(), mapValuesFromJsonImports),"protected");
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
                List.of(new TsMethodParameter("json",new TsTypePrimitive("any")),new TsMethodParameter("idToDataMap",new TsTypePrimitive("any")), new TsMethodParameter("dataCreator",new TsTypeClass(dataCreatorTsClass))),
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
        String createCode="return new AttributeAccessor<"+ attributeTsType.construct()+","+tsClassName.getName()+">("+tsClassName.getName()+"."+attributeVariableName+"Metadata,this,\""+attributeVariableName+"\");";
        return new TsMethod(attributeVariableName+"Accessor",
                List.of(),
                new TsMethodResult(new TsTypeClass(attributeAccessorClass, attributeTsType, new TsTypeClass(tsClassName))),new TsMethodCode(createCode),"public");
    }

    private TsAttribute getTsAttributeMetadata(String attributeVariableName, Attribute<?, ?> attribute){
        ArrayList<TsValue> constructorParameters = new ArrayList<>();
        constructorParameters.add(new TsValueString(attribute.internal_getPreferredLabelText(Locale.ENGLISH)));
        constructorParameters.add(new TsValueString(attribute.internal_getPreferredLabelText(Locale.GERMAN)));
        constructorParameters.add(new TsValueEnum(attributeToTsMapperManager.getAttributeTypeValue(attribute),attributeTypeEnumTsEnum));
        return new TsAttribute(attributeVariableName+"Metadata", new TsTypeClass(attributeMetadataTsClass,getTsType(attribute)),true,true,true, constructorParameters);
    }

    private TsType getTsType(Attribute<?, ?> attribute){
        return attributeToTsMapperManager.getTsType(attribute);
    }

}
