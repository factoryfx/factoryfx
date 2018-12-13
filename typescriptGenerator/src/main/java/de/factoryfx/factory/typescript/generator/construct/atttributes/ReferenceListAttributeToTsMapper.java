package de.factoryfx.factory.typescript.generator.construct.atttributes;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.factory.typescript.generator.ts.*;

import java.util.Map;
import java.util.Set;

public class ReferenceListAttributeToTsMapper implements AttributeToTsMapper {
    private final Map<Class<? extends Data>,TsClassConstructed> dataToConfigTs;

    public ReferenceListAttributeToTsMapper(Map<Class<? extends Data>, TsClassConstructed> dataToConfigTs) {
        this.dataToConfigTs = dataToConfigTs;
    }

    @Override
    public TsType getTsType(Attribute<?,?> attribute) {
        Class referenceClass = ((ReferenceListAttribute) attribute).internal_getReferenceClass();
        return new TsTypeArray(new TsTypeClass(dataToConfigTs.get(referenceClass)));
    }

    @Override
    public String getMapFromJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        Class referenceClass = ((ReferenceListAttribute) attribute).internal_getReferenceClass();
        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
        jsonImports.add(dataClass);
        return ("this."+attributeVariableName+"=<"+dataClass.getName()+"[]>dataCreator.createDataList(json."+attributeVariableName+",idToDataMap);\n");
    }

    @Override
    public String getMapToJsonExpression(String attributeVariableName, Attribute<?,?> attribute, Set<TsFile> jsonImports) {
        Class referenceClass = ((ReferenceListAttribute) attribute).internal_getReferenceClass();
        TsClassConstructed dataClass = dataToConfigTs.get(referenceClass);
        jsonImports.add(dataClass);
        return "result."+attributeVariableName+"=this.mapAttributeDataListToJson(idToDataMap,this."+attributeVariableName+");\n";
    }
}
