package io.github.factoryfx.factory.typescript.generator.construct.atttributes;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.*;
import io.github.factoryfx.factory.attribute.primitive.*;
import io.github.factoryfx.factory.attribute.primitive.list.*;
import io.github.factoryfx.factory.attribute.time.*;
import io.github.factoryfx.factory.attribute.types.*;
import io.github.factoryfx.factory.metadata.AttributeMetadata;
import io.github.factoryfx.factory.typescript.generator.ts.*;

import java.util.*;

public class AttributeToTsMapperManager {

    public static class AttributeToTsMapperManagerCreator<R extends FactoryBase<?,R>>{
        public AttributeToTsMapperManager create(Map<Class<? extends FactoryBase<?,R>>, TsClassConstructed> dataToOverrideTs, Set<TsEnumConstructed> tsEnums, TsFile dataType){
            HashMap<Class<?>, AttributeToTsMapper> classToInfo = new HashMap<>();
            classToInfo.put(FactoryAttribute.class, new FactoryAttributeToTsMapper<>(dataToOverrideTs,dataType));
            classToInfo.put(FactoryListAttribute.class, new FactoryListAttributeToTsMapper<>(dataToOverrideTs,dataType));

            classToInfo.put(FactoryViewAttribute.class, new ViewAttributeToTsMapper(dataType));
            classToInfo.put(FactoryViewListAttribute.class, new ViewListAttributeToTsMapper(dataType));

            classToInfo.put(ByteArrayAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(I18nAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(EncryptedStringAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(DoubleAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(ByteAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(BooleanAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.BOOLEAN));
            classToInfo.put(LocalDateAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.DATE,"mapLocalDateFromJson","mapLocalDateToJson"));
            classToInfo.put(EnumAttribute.class, new EnumAttributeToTsMapper(tsEnums));
            classToInfo.put(CharAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(LongAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.BIGINT,"mapLongFromJson","mapLongToJson"));
            classToInfo.put(StringAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(IntegerAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(LocalDateTimeAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.DATE,"mapLocalDateTimeFromJson","mapLocalDateTimeToJson"));
            classToInfo.put(LocaleAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(DurationAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(FileContentAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(LocalTimeAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(ShortAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(PasswordAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(URIAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(BigDecimalAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(FloatAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(InstantAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.DATE,"mapInstantFromJson","mapInstantToJson"));
            classToInfo.put(BigIntegerAttribute.class, new ValueAttributeToTsMapper(TsTypePrimitive.BIGINT));

            classToInfo.put(CharListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(LongListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(EnumListAttribute.class, new EnumListAttributeToTsMapper(tsEnums));
            classToInfo.put(ByteListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(FloatListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(IntegerListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(StringListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.STRING));
            classToInfo.put(DoubleListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(ShortListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.NUMBER));
            classToInfo.put(URIListAttribute.class, new ValueListAttributeToTsMapper(TsTypePrimitive.STRING));

            Set<Class<?>> ignoredAttributes = new HashSet<>();
            ignoredAttributes.add(ObjectValueAttribute.class);

            return new AttributeToTsMapperManager(classToInfo,ignoredAttributes);

        }
    }

    private final Map<Class<?>, AttributeToTsMapper> attributeClassToMapper;
    private final Set<Class<?>> ignoredAttributes;

    public AttributeToTsMapperManager(Map<Class<?>, AttributeToTsMapper> attributeClassToMapper, Set<Class<?>> ignoredAttributes) {
        this.attributeClassToMapper = attributeClassToMapper;
        this.ignoredAttributes = ignoredAttributes;
    }

    public TsType getTsType(AttributeMetadata metadata){
        AttributeToTsMapper attributeToTsMapper = getAttributeToTsMapper(metadata.attributeClass);
        if (attributeToTsMapper !=null){
            return  attributeToTsMapper.getTsType(metadata);
        }
        throw new IllegalStateException("unknown attribute type"+metadata.attributeClass);
    }

    public boolean isMappable(Class<?> attribute){
        return !ignoredAttributes.contains(attribute);
    }

    public String getMapFromJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        AttributeToTsMapper attributeToTsMapper = getAttributeToTsMapper(metadata.attributeClass);
        if (attributeToTsMapper ==null){
            throw new IllegalStateException("unsupported attribute: "+metadata.attributeClass);
        }
        return attributeToTsMapper.getMapFromJsonExpression(metadata,jsonImports);
    }

    public String getMapToJsonExpression(AttributeMetadata metadata, Set<TsFile> jsonImports) {
        AttributeToTsMapper attributeToTsMapper = getAttributeToTsMapper(metadata.attributeClass);
        if (attributeToTsMapper ==null){
            throw new IllegalStateException("unsupported attribute: "+metadata.attributeClass);
        }
        return attributeToTsMapper.getMapToJsonExpression(metadata,jsonImports);
    }

    public Set<String> getAttributeTypeValues() {
        Set<String> result = new HashSet<>();
        for (Class<?> clazz : this.attributeClassToMapper.keySet()) {
            result.add(clazz.getSimpleName());
        }
        return result;
    }

    public String getAttributeTypeValue(AttributeMetadata attribute) {
        return attribute.attributeClass.getSimpleName();
    }

    private AttributeToTsMapper getAttributeToTsMapper(Class<?> attributeClass) {
        if (FactoryBaseAttribute.class.isAssignableFrom(attributeClass)){
            return attributeClassToMapper.get(FactoryAttribute.class);
        }
        if (FactoryListBaseAttribute.class.isAssignableFrom(attributeClass)){
            return attributeClassToMapper.get(FactoryListAttribute.class);
        }
        return attributeClassToMapper.get(attributeClass);
    }

}
