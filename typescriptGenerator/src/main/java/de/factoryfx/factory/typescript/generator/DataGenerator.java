package de.factoryfx.factory.typescript.generator;

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

public class DataGenerator {

    private final Class<? extends Data> clazz;
    private final TsClass dataTsClass;
    private final TsClass dataCreatorTsClass;
    private final HashMap<Class<? extends Data>,TsClassConstructed> dataToTs;

    public DataGenerator(Class<? extends Data> clazz, HashMap<Class<? extends Data>, TsClassConstructed> dataToTs,TsClass dataTsClass, TsClass dataCreatorTsClass) {
        this.clazz = clazz;
        this.dataTsClass = dataTsClass;
        this.dataToTs = dataToTs;
        this.dataCreatorTsClass = dataCreatorTsClass;
    }


    public TsClass complete(TsClassConstructed tsClass){
        ArrayList<TsAttribute> attributes = new ArrayList<>();
        Data data = DataDictionary.getDataDictionary(clazz).newInstance();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
            attributes.add(getTsAttribute(attributeVariableName,attribute));
        });

        StringBuilder fromJsonCode=new StringBuilder();
        ArrayList<TsClass> mapValuesFromJsonImports = new ArrayList<>();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {

            if (attribute instanceof ReferenceAttribute){
                Class referenceClass = ((ReferenceAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("this."+attributeVariableName+"=<"+ dataClass.getName()+">dataCreator.createData(json."+attributeVariableName+".v,idToDataMap);\n");
                return;
            }
            if (attribute instanceof ReferenceListAttribute){
                Class referenceClass = ((ReferenceListAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToTs.get(referenceClass);
                mapValuesFromJsonImports.add(dataClass);
                fromJsonCode.append("this."+attributeVariableName+"=<"+ dataClass.getName()+"[]>dataCreator.createDataList(json."+attributeVariableName+",idToDataMap);\n");
                return;
            }

            fromJsonCode.append("this."+attributeVariableName+"=json."+attributeVariableName+".v;\n");
//            fromJsonCode.append("       this."+attributeVariableName+"=json."+attributeVariableName+".v;\n");
//            fromJsonCode.append("       if (!json."+attributeVariableName+".v) this."+attributeVariableName+"=null;\n");
        });

        ArrayList<TsMethod> methods = new ArrayList<>();
        methods.add(new TsMethod("mapValuesFromJson",
                List.of(new TsMethodParameterPrimitive("json","any"),new TsMethodParameterPrimitive("idToDataMap","any"), new TsMethodParameterClass("dataCreator",dataCreatorTsClass)),
                new TsMethodResultVoid(),new TsMethodCode(fromJsonCode.toString(), mapValuesFromJsonImports),"protected"));


        StringBuilder toJsonCode=new StringBuilder();
        ArrayList<TsClass> toValuesFromJsonImports = new ArrayList<>();
        data.internal().visitAttributesFlat((attributeVariableName, attribute) -> {

            if (attribute instanceof ReferenceAttribute){
                Class referenceClass = ((ReferenceAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToTs.get(referenceClass);
                toValuesFromJsonImports.add(dataClass);
                toJsonCode.append("result."+attributeVariableName+"=this.mapAttributeDataToJson(idToDataMap,this."+attributeVariableName+");\n");
                return;
            }
            if (attribute instanceof ReferenceListAttribute){
                Class referenceClass = ((ReferenceListAttribute) attribute).internal_getReferenceClass();
                TsClassConstructed dataClass = dataToTs.get(referenceClass);
                toValuesFromJsonImports.add(dataClass);
                toJsonCode.append("result."+attributeVariableName+"=this.mapAttributeDataListToJson(idToDataMap,this."+attributeVariableName+");\n");
                return;
            }
            toJsonCode.append("result."+attributeVariableName+"=this.mapAttributeValueToJson(idToDataMap,this."+attributeVariableName+");\n");
        });
        methods.add(new TsMethod("mapValuesToJson",
                List.of(new TsMethodParameterPrimitive("idToDataMap","any"),new TsMethodParameterPrimitive("result","any")),
                new TsMethodResultVoid(),new TsMethodCode(toJsonCode.toString(),toValuesFromJsonImports),"protected"));



        tsClass.parent=dataTsClass;
        tsClass.attributes=attributes;
        tsClass.methods=methods;

        return tsClass;
    }

    private TsAttribute getTsAttribute(String attributeVariableName, Attribute<?, ?> attribute){
        if (attribute instanceof StringAttribute){
            return new TsAttributePrimitive(attributeVariableName,"string");
        }
        if (attribute instanceof ReferenceAttribute){
            ReferenceAttribute<?,?> referenceAttribute = (ReferenceAttribute<?,?>) attribute;
            return new TsAttributeClass(attributeVariableName,dataToTs.get(referenceAttribute.internal_getReferenceClass()));
        }
        if (attribute instanceof ReferenceListAttribute){
            ReferenceListAttribute<?,?> referenceListAttribute = (ReferenceListAttribute<?,?>) attribute;
            return new TsAttributeArrayClass(attributeVariableName,dataToTs.get(referenceListAttribute.internal_getReferenceClass()));
        }

        //TODO should support custom non default attributes
        throw new IllegalStateException("unkown attribute type"+attribute);

    }
}
