//generated code don't edit manually
export enum AttributeType  {

BooleanAttribute="BooleanAttribute",
LocalDateTimeAttribute="LocalDateTimeAttribute",
LocalDateAttribute="LocalDateAttribute",
LocalTimeAttribute="LocalTimeAttribute",
FactoryPolymorphicAttribute="FactoryPolymorphicAttribute",
ByteAttribute="ByteAttribute",
URIListAttribute="URIListAttribute",
StringAttribute="StringAttribute",
CharAttribute="CharAttribute",
FactoryPolymorphicListAttribute="FactoryPolymorphicListAttribute",
EncryptedStringAttribute="EncryptedStringAttribute",
EnumAttribute="EnumAttribute",
EnumListAttribute="EnumListAttribute",
LongListAttribute="LongListAttribute",
I18nAttribute="I18nAttribute",
FileContentAttribute="FileContentAttribute",
CharListAttribute="CharListAttribute",
LongAttribute="LongAttribute",
URIAttribute="URIAttribute",
FloatAttribute="FloatAttribute",
PasswordAttribute="PasswordAttribute",
IntegerListAttribute="IntegerListAttribute",
DoubleListAttribute="DoubleListAttribute",
ShortListAttribute="ShortListAttribute",
ShortAttribute="ShortAttribute",
BigDecimalAttribute="BigDecimalAttribute",
ByteArrayAttribute="ByteArrayAttribute",
FactoryAttribute="FactoryAttribute",
FloatListAttribute="FloatListAttribute",
IntegerAttribute="IntegerAttribute",
InstantAttribute="InstantAttribute",
DoubleAttribute="DoubleAttribute",
FactoryListAttribute="FactoryListAttribute",
BigIntegerAttribute="BigIntegerAttribute",
LocaleAttribute="LocaleAttribute",
StringListAttribute="StringListAttribute",
DurationAttribute="DurationAttribute",
ByteListAttribute="ByteListAttribute"

}
export namespace AttributeType {
    export function fromJson(json: string): AttributeType{
        return AttributeType[json];
    }
    export function toJson(value: AttributeType): string{
        if (value) return value.toString();
    }
}
