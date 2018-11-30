package de.factoryfx.factory.typescript.generator.construct;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.typescript.generator.ts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DataGeneratedTs {

    private final Class<? extends Data> clazz;
    private final TsClassFile dataTsClass;
    private final TsClassFile dataCreatorTsClass;
    private final TsClassFile attributeMetadataTsClass;
    private final HashMap<Class<? extends Data>,TsClassConstructed> dataToOverrideTs;
    private final TsClassFile attributeAccessorClass;

    public DataGeneratedTs(Class<? extends Data> clazz, HashMap<Class<? extends Data>, TsClassConstructed> dataToOverrideTs, TsClassFile dataTsClass, TsClassFile dataCreatorTsClass, TsClassFile attributeTsClass, TsClassFile attributeAccessorClass) {
        this.clazz = clazz;
        this.dataTsClass = dataTsClass;
        this.dataToOverrideTs = dataToOverrideTs;
        this.dataCreatorTsClass = dataCreatorTsClass;
        this.attributeMetadataTsClass = attributeTsClass;
        this.attributeAccessorClass = attributeAccessorClass;
    }


    public TsClassFile complete(TsClassConstructed tsClass){
        ArrayList<TsAttribute> attributes = new ArrayList<>();
        Data data = DataDictionary.getDataDictionary(clazz).newInstance();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            attributes.add(getTsAttribute(attributeVariableName,attribute));
        });

        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            attributes.add(getTsAttributeMetadata(attributeVariableName,attribute));
        });




        ArrayList<TsMethod> methods = new ArrayList<>();

        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            methods.add(getTsAttributeAccessor(attributeVariableName,attribute,tsClass));
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


    private TsMethod createListAttributeAccessor(Data data, TsClassConstructed tsClass) {
        StringBuilder code=new StringBuilder("let result: AttributeAccessor<any,"+tsClass.getName()+">[]=[];\n");
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            code.append("result.push(this.").append(attributeVariableName).append("Accessor());\n");
        });
        code.append("return result;");
        return new TsMethod("listAttributeAccessor", List.of(),
                new TsMethodResult(new TsTypeArray(new TsTypeClass(attributeAccessorClass,new TsTypePrimitive("any"),new TsTypeClass(tsClass)))),new TsMethodCode(code.toString(), List.of()),"protected");
    }

    private TsMethod createCollectChildren(Data data) {
        StringBuilder fromJsonCode=new StringBuilder();
        ArrayList<TsClassFile> mapValuesFromJsonImports = new ArrayList<>();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            if (attribute instanceof ReferenceAttribute){
                Class referenceClass = ((ReferenceAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("this.collectDataChildren(this.").append(attributeVariableName).append(",idToDataMap);\n");
                return;
            }
            if (attribute instanceof ReferenceListAttribute){
                Class referenceClass = ((ReferenceListAttribute) attribute).internal_getReferenceClass();
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

    private TsMethod createMapValuesFromJson(Data data) {
        StringBuilder fromJsonCode=new StringBuilder();
        ArrayList<TsClassFile> mapValuesFromJsonImports = new ArrayList<>();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {

            if (attribute instanceof ReferenceAttribute){
                Class referenceClass = ((ReferenceAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("this.").append(attributeVariableName).append("=<").append(dataClass.getName()).append(">dataCreator.createData(json.").append(attributeVariableName).append(".v,idToDataMap);\n");
                return;
            }
            if (attribute instanceof ReferenceListAttribute){
                Class referenceClass = ((ReferenceListAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("this.").append(attributeVariableName).append("=<").append(dataClass.getName()).append("[]>dataCreator.createDataList(json.").append(attributeVariableName).append(",idToDataMap);\n");
                return;
            }

            fromJsonCode.append("this.").append(attributeVariableName).append("=json.").append(attributeVariableName).append(".v;\n");
//            fromJsonCode.append("       this."+attributeVariableName+"=json."+attributeVariableName+".v;\n");
//            fromJsonCode.append("       if (!json."+attributeVariableName+".v) this."+attributeVariableName+"=null;\n");
        });

        return new TsMethod("mapValuesFromJson",
                List.of(new TsMethodParameter("json",new TsTypePrimitive("any")),new TsMethodParameter("idToDataMap",new TsTypePrimitive("any")), new TsMethodParameter("dataCreator",new TsTypeClass(dataCreatorTsClass))),
                new TsMethodResultVoid(),new TsMethodCode(fromJsonCode.toString(), mapValuesFromJsonImports),"protected");
    }

    private TsMethod createAddMapValueToJson(Data data) {
        StringBuilder toJsonCode=new StringBuilder();
        ArrayList<TsClassFile> toValuesFromJsonImports = new ArrayList<>();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {

            if (attribute instanceof ReferenceAttribute){
                Class referenceClass = ((ReferenceAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                toValuesFromJsonImports.add(dataClass);
                toJsonCode.append("result.").append(attributeVariableName).append("=this.mapAttributeDataToJson(idToDataMap,this.").append(attributeVariableName).append(");\n");
                return;
            }
            if (attribute instanceof ReferenceListAttribute){
                Class referenceClass = ((ReferenceListAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToOverrideTs.get(referenceClass);
                toValuesFromJsonImports.add(dataClass);
                toJsonCode.append("result.").append(attributeVariableName).append("=this.mapAttributeDataListToJson(idToDataMap,this.").append(attributeVariableName).append(");\n");
                return;
            }
            toJsonCode.append("result.").append(attributeVariableName).append("=this.mapAttributeValueToJson(idToDataMap,this.").append(attributeVariableName).append(");\n");
        });
        return new TsMethod("mapValuesToJson",
                List.of(new TsMethodParameter("idToDataMap",new TsTypePrimitive("any")),new TsMethodParameter("result",new TsTypePrimitive("any"))),
                new TsMethodResultVoid(),new TsMethodCode(toJsonCode.toString(),toValuesFromJsonImports),"protected");
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
        ArrayList<String> constructorParameters = new ArrayList<>();
        String enLabel = escapeTsString(attribute.internal_getPreferredLabelText(Locale.ENGLISH));
        String deLabel = escapeTsString(attribute.internal_getPreferredLabelText(Locale.GERMAN));
        constructorParameters.add(enLabel);
        constructorParameters.add(deLabel);
        return new TsAttribute(attributeVariableName+"Metadata", new TsTypeClass(attributeMetadataTsClass,getTsType(attribute)),true,true,true, constructorParameters);
    }

    private TsType getTsType(Attribute<?, ?> attribute){

        if (attribute instanceof StringAttribute){
            return new TsTypePrimitive("string");
        }
        if (attribute instanceof ReferenceAttribute){
            ReferenceAttribute<?,?> referenceAttribute = (ReferenceAttribute<?,?>) attribute;
            return new TsTypeClass(dataToOverrideTs.get(referenceAttribute.internal_getReferenceClass()));
        }
        if (attribute instanceof ReferenceListAttribute){
            ReferenceListAttribute<?,?> referenceListAttribute = (ReferenceListAttribute<?,?>) attribute;
            return new TsTypeArray(new TsTypeClass(dataToOverrideTs.get(referenceListAttribute.internal_getReferenceClass())));
        }

        throw new IllegalStateException("unknown attribute type"+attribute);
        //TODO should support custom non default attributes
    }

    private static final Escaper TS_ESCAPER =
            Escapers.builder()
                    .addEscape('\\', "\\\\")
                    .addEscape('"', "\\\"")
                    .addEscape('\'', "\\\'")
                    .build();

    private String escapeTsString(String text){
        return "'"+TS_ESCAPER.escape(text)+"'";
    }
}
