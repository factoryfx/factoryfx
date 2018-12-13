//generated code don't edit manually
export enum AttributeType  {

BooleanAttribute="BooleanAttribute",
LocalDateTimeAttribute="LocalDateTimeAttribute",
FactoryReferenceListAttribute="FactoryReferenceListAttribute",
LocalTimeAttribute="LocalTimeAttribute",
LocalDateAttribute="LocalDateAttribute",
DataReferenceAttribute="DataReferenceAttribute",
FactoryReferenceAttribute="FactoryReferenceAttribute",
ByteAttribute="ByteAttribute",
URIListAttribute="URIListAttribute",
StringAttribute="StringAttribute",
CharAttribute="CharAttribute",
DataReferenceListAttribute="DataReferenceListAttribute",
EnumListAttribute="EnumListAttribute",
EnumAttribute="EnumAttribute",
EncryptedStringAttribute="EncryptedStringAttribute",
LongListAttribute="LongListAttribute",
I18nAttribute="I18nAttribute",
FileContentAttribute="FileContentAttribute",
CharListAttribute="CharListAttribute",
LongAttribute="LongAttribute",
URIAttribute="URIAttribute",
FloatAttribute="FloatAttribute",
PasswordAttribute="PasswordAttribute",
ShortListAttribute="ShortListAttribute",
IntegerListAttribute="IntegerListAttribute",
DoubleListAttribute="DoubleListAttribute",
ShortAttribute="ShortAttribute",
BigDecimalAttribute="BigDecimalAttribute",
ByteArrayAttribute="ByteArrayAttribute",
FloatListAttribute="FloatListAttribute",
IntegerAttribute="IntegerAttribute",
InstantAttribute="InstantAttribute",
DoubleAttribute="DoubleAttribute",
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
