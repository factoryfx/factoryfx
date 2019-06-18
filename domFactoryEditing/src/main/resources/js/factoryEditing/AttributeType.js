//generated code don't edit manually
export var AttributeType;
(function (AttributeType) {
    AttributeType["BooleanAttribute"] = "BooleanAttribute";
    AttributeType["LocalDateTimeAttribute"] = "LocalDateTimeAttribute";
    AttributeType["LocalTimeAttribute"] = "LocalTimeAttribute";
    AttributeType["LocalDateAttribute"] = "LocalDateAttribute";
    AttributeType["FactoryPolymorphicAttribute"] = "FactoryPolymorphicAttribute";
    AttributeType["URIListAttribute"] = "URIListAttribute";
    AttributeType["ByteAttribute"] = "ByteAttribute";
    AttributeType["StringAttribute"] = "StringAttribute";
    AttributeType["CharAttribute"] = "CharAttribute";
    AttributeType["FactoryPolymorphicListAttribute"] = "FactoryPolymorphicListAttribute";
    AttributeType["EncryptedStringAttribute"] = "EncryptedStringAttribute";
    AttributeType["EnumListAttribute"] = "EnumListAttribute";
    AttributeType["EnumAttribute"] = "EnumAttribute";
    AttributeType["LongListAttribute"] = "LongListAttribute";
    AttributeType["I18nAttribute"] = "I18nAttribute";
    AttributeType["FileContentAttribute"] = "FileContentAttribute";
    AttributeType["CharListAttribute"] = "CharListAttribute";
    AttributeType["LongAttribute"] = "LongAttribute";
    AttributeType["URIAttribute"] = "URIAttribute";
    AttributeType["FloatAttribute"] = "FloatAttribute";
    AttributeType["PasswordAttribute"] = "PasswordAttribute";
    AttributeType["DoubleListAttribute"] = "DoubleListAttribute";
    AttributeType["IntegerListAttribute"] = "IntegerListAttribute";
    AttributeType["ShortListAttribute"] = "ShortListAttribute";
    AttributeType["ShortAttribute"] = "ShortAttribute";
    AttributeType["BigDecimalAttribute"] = "BigDecimalAttribute";
    AttributeType["ByteArrayAttribute"] = "ByteArrayAttribute";
    AttributeType["FactoryAttribute"] = "FactoryAttribute";
    AttributeType["IntegerAttribute"] = "IntegerAttribute";
    AttributeType["FloatListAttribute"] = "FloatListAttribute";
    AttributeType["InstantAttribute"] = "InstantAttribute";
    AttributeType["DoubleAttribute"] = "DoubleAttribute";
    AttributeType["FactoryListAttribute"] = "FactoryListAttribute";
    AttributeType["BigIntegerAttribute"] = "BigIntegerAttribute";
    AttributeType["LocaleAttribute"] = "LocaleAttribute";
    AttributeType["StringListAttribute"] = "StringListAttribute";
    AttributeType["DurationAttribute"] = "DurationAttribute";
    AttributeType["ByteListAttribute"] = "ByteListAttribute";
})(AttributeType || (AttributeType = {}));
(function (AttributeType) {
    function fromJson(json) {
        return AttributeType[json];
    }
    AttributeType.fromJson = fromJson;
    function toJson(value) {
        if (value)
            return value.toString();
    }
    AttributeType.toJson = toJson;
})(AttributeType || (AttributeType = {}));
