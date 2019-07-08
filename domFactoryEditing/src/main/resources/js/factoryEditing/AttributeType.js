//generated code don't edit manually
export var AttributeType;
(function (AttributeType) {
    AttributeType["BooleanAttribute"] = "BooleanAttribute";
    AttributeType["LocalDateTimeAttribute"] = "LocalDateTimeAttribute";
    AttributeType["LocalDateAttribute"] = "LocalDateAttribute";
    AttributeType["LocalTimeAttribute"] = "LocalTimeAttribute";
    AttributeType["FactoryPolymorphicAttribute"] = "FactoryPolymorphicAttribute";
    AttributeType["ByteAttribute"] = "ByteAttribute";
    AttributeType["URIListAttribute"] = "URIListAttribute";
    AttributeType["StringAttribute"] = "StringAttribute";
    AttributeType["CharAttribute"] = "CharAttribute";
    AttributeType["FactoryPolymorphicListAttribute"] = "FactoryPolymorphicListAttribute";
    AttributeType["EncryptedStringAttribute"] = "EncryptedStringAttribute";
    AttributeType["EnumAttribute"] = "EnumAttribute";
    AttributeType["EnumListAttribute"] = "EnumListAttribute";
    AttributeType["LongListAttribute"] = "LongListAttribute";
    AttributeType["I18nAttribute"] = "I18nAttribute";
    AttributeType["FileContentAttribute"] = "FileContentAttribute";
    AttributeType["CharListAttribute"] = "CharListAttribute";
    AttributeType["LongAttribute"] = "LongAttribute";
    AttributeType["URIAttribute"] = "URIAttribute";
    AttributeType["FloatAttribute"] = "FloatAttribute";
    AttributeType["PasswordAttribute"] = "PasswordAttribute";
    AttributeType["IntegerListAttribute"] = "IntegerListAttribute";
    AttributeType["DoubleListAttribute"] = "DoubleListAttribute";
    AttributeType["ShortListAttribute"] = "ShortListAttribute";
    AttributeType["ShortAttribute"] = "ShortAttribute";
    AttributeType["BigDecimalAttribute"] = "BigDecimalAttribute";
    AttributeType["ByteArrayAttribute"] = "ByteArrayAttribute";
    AttributeType["FactoryAttribute"] = "FactoryAttribute";
    AttributeType["FloatListAttribute"] = "FloatListAttribute";
    AttributeType["IntegerAttribute"] = "IntegerAttribute";
    AttributeType["InstantAttribute"] = "InstantAttribute";
    AttributeType["DoubleAttribute"] = "DoubleAttribute";
    AttributeType["FactoryListAttribute"] = "FactoryListAttribute";
    AttributeType["BigIntegerAttribute"] = "BigIntegerAttribute";
    AttributeType["LocaleAttribute"] = "LocaleAttribute";
    AttributeType["StringListAttribute"] = "StringListAttribute";
    AttributeType["DurationAttribute"] = "DurationAttribute";
    AttributeType["ByteListAttribute"] = "ByteListAttribute";
    AttributeType["FactoryViewAttribute"] = "FactoryViewAttribute";
    AttributeType["FactoryViewListAttribute"] = "FactoryViewListAttribute";
})(AttributeType || (AttributeType = {}));
(function (AttributeType) {
    function fromJson(json) {
        return AttributeType[json];
    }
    AttributeType.fromJson = fromJson;
    function toJson(value) {
        if (value)
            return value.toString();
        return null;
    }
    AttributeType.toJson = toJson;
})(AttributeType || (AttributeType = {}));
