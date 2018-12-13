package de.factoryfx.factory.typescript.generator.construct.atttributes;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.*;
import de.factoryfx.data.attribute.primitive.*;
import de.factoryfx.data.attribute.primitive.list.*;
import de.factoryfx.data.attribute.time.*;
import de.factoryfx.data.attribute.types.*;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.atrribute.FactoryViewListReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryViewReferenceAttribute;
import de.factoryfx.factory.typescript.generator.ts.*;

import java.util.*;

public class AttributeToTsMapperManager {

    private final Map<Class<? extends Attribute>, AttributeToTsMapper> classToInfo;
    private final Set<Class<? extends Attribute>> ignoredAttributes;

    public AttributeToTsMapperManager(Map<Class<? extends Attribute>, AttributeToTsMapper> classToInfo, Set<Class<? extends Attribute>> ignoredAttributes) {
        this.classToInfo = classToInfo;
        this.ignoredAttributes = ignoredAttributes;
    }

    public TsType getTsType(Attribute<?,?> attribute){
        AttributeToTsMapper attributeToTsMapper = classToInfo.get(attribute.getClass());
        if (attributeToTsMapper !=null){
            return  attributeToTsMapper.getTsType(attribute);
        }
        throw new IllegalStateException("unknown attribute type"+attribute);
    }

    public static Map<Class<? extends Attribute>, AttributeToTsMapper> createAttributeInfoMap(Map<Class<? extends Data>, TsClassConstructed> dataToOverrideTs, Set<TsEnumConstructed> tsEnums){
        HashMap<Class<? extends Attribute>, AttributeToTsMapper> result = new HashMap<>();
        result.put(DataReferenceAttribute.class, new ReferenceAttributeToTsMapper(dataToOverrideTs));
        result.put(DataReferenceListAttribute.class, new ReferenceListAttributeToTsMapper(dataToOverrideTs));
        result.put(FactoryReferenceAttribute.class, new ReferenceAttributeToTsMapper(dataToOverrideTs));
        result.put(FactoryReferenceListAttribute.class, new ReferenceListAttributeToTsMapper(dataToOverrideTs));

        result.put(ByteArrayAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(I18nAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(EncryptedStringAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(DoubleAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(ByteAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(BooleanAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.BOOLEAN));
        result.put(LocalDateAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.DATE,"mapLocalDateFromJson","mapLocalDateToJson"));
        result.put(EnumAttribute.class, new EnumAttributeToTsMapper(tsEnums));
        result.put(CharAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(LongAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(StringAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(IntegerAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(LocalDateTimeAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.DATE,"mapLocalDateTimeFromJson","mapLocalDateTimeToJson"));
        result.put(LocaleAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(DurationAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(FileContentAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(LocalTimeAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(ShortAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(PasswordAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(URIAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(BigDecimalAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(FloatAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(InstantAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.DATE,"mapInstantFromJson","mapInstantToJson"));
        result.put(BigIntegerAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.BIGINT));

        result.put(CharListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(LongListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(EnumListAttribute.class, new EnumListAttributeToTsMapper(tsEnums));
        result.put(ByteListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(FloatListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(IntegerListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(StringListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.STRING));
        result.put(DoubleListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(ShortListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
        result.put(URIListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.STRING));
        return result;
    }

    public static Set<Class<? extends Attribute>> createAttributeIgnoreSet(){
        Set<Class<? extends Attribute>> result = new HashSet<>();
        result.add(ObjectValueAttribute.class);
        result.add(DataViewReferenceAttribute.class);
        result.add(DataViewListReferenceAttribute.class);
        result.add(FactoryViewReferenceAttribute.class);
        result.add(FactoryViewListReferenceAttribute.class);
        return result;
    }

    public boolean isMappable(Class<? extends Attribute> attribute){
        return !ignoredAttributes.contains(attribute);
    }

    public String getMapFromJsonExpression(String attributeVariableName, Attribute attribute, Set<TsFile> jsonImports) {
        AttributeToTsMapper attributeToTsMapper = classToInfo.get(attribute.getClass());
        if (attributeToTsMapper ==null){
            throw new IllegalStateException("unsupported attribute: "+attribute.getClass());
        }
        return attributeToTsMapper.getMapFromJsonExpression(attributeVariableName,attribute,jsonImports);
    }

    public String getMapToJsonExpression(String attributeVariableName, Attribute attribute, Set<TsFile> jsonImports) {
        AttributeToTsMapper attributeToTsMapper = classToInfo.get(attribute.getClass());
        if (attributeToTsMapper ==null){
            throw new IllegalStateException("unsupported attribute: "+attribute.getClass());
        }
        return attributeToTsMapper.getMapToJsonExpression(attributeVariableName,attribute,jsonImports);
    }

    public Set<String> getAttributeTypeValues() {
        Set<String> result = new HashSet<>();
        for (Class<? extends Attribute> clazz : this.classToInfo.keySet()) {
            result.add(clazz.getSimpleName());
        }
        return result;
    }

    public String getAttributeTypeValue(Attribute<?, ?> attribute) {
        return attribute.getClass().getSimpleName();
    }
}
