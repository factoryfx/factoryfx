//generated code don't edit manually
export enum AttributeType  {

BooleanAttribute="BooleanAttribute",
LocalDateTimeAttribute="LocalDateTimeAttribute",
LocalDateAttribute="LocalDateAttribute",
LocalTimeAttribute="LocalTimeAttribute",
URIListAttribute="URIListAttribute",
ByteAttribute="ByteAttribute",
StringAttribute="StringAttribute",
CharAttribute="CharAttribute",
EncryptedStringAttribute="EncryptedStringAttribute",
EnumListAttribute="EnumListAttribute",
EnumAttribute="EnumAttribute",
LongListAttribute="LongListAttribute",
I18nAttribute="I18nAttribute",
FileContentAttribute="FileContentAttribute",
CharListAttribute="CharListAttribute",
LongAttribute="LongAttribute",
URIAttribute="URIAttribute",
FloatAttribute="FloatAttribute",
PasswordAttribute="PasswordAttribute",
DoubleListAttribute="DoubleListAttribute",
ShortListAttribute="ShortListAttribute",
IntegerListAttribute="IntegerListAttribute",
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
