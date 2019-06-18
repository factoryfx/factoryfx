//generated code don't edit manually
import { Data } from "../../../../../../../../util/Data";
import { AttributeAccessor } from "../../../../../../../../util/AttributeAccessor";
import { StaticAttributeValueAccessor } from "../../../../../../../../util/StaticAttributeValueAccessor";
import { ExampleEnum } from "./ExampleEnum";
import { DataCreator } from "../../../../../../../../util/DataCreator";
import { DynamicDataDictionary } from "../../../../../../../../util/DynamicDataDictionary";
import { AttributeType } from "../../../../../../../../util/AttributeType";
import { AttributeMetadata } from "../../../../../../../../util/AttributeMetadata";

export abstract class ExampleDataAllGenerated  extends Data {

    public byteArrayAttribute: string = null;
    public i18nAttribute: string = null;
    public encryptedStringAttribute: string = null;
    public doubleAttribute: number = null;
    public byteAttribute: number = null;
    public booleanAttribute: boolean = null;
    public localDateAttribute: Date = null;
    public enumAttribute: ExampleEnum = null;
    public charAttribute: string = null;
    public longAttribute: number = null;
    public stringAttribute: string = null;
    public integerAttribute: number = null;
    public localDateTimeAttribute: Date = null;
    public localeAttribute: string = null;
    public durationAttribute: string = null;
    public fileContentAttribute: string = null;
    public localTimeAttribute: string = null;
    public shortAttribute: number = null;
    public passwordAttribute: string = null;
    public uriAttribute: string = null;
    public bigDecimalAttribute: string = null;
    public floatAttribute: number = null;
    public stringListAttribute: string[] = null;
    public enumListAttribute: ExampleEnum[] = null;
    public instantAttribute: Date = null;
    public bigIntegerAttribute: bigint = null;
    public factoryPolymorphicAttribute: Data = null;
    public static readonly byteArrayAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.ByteArrayAttribute,false,[]);
    public static readonly i18nAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.I18nAttribute,false,[]);
    public static readonly encryptedStringAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.EncryptedStringAttribute,false,[]);
    public static readonly doubleAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.DoubleAttribute,false,[]);
    public static readonly byteAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.ByteAttribute,false,[]);
    public static readonly booleanAttributeMetadata: AttributeMetadata<boolean>= new AttributeMetadata<boolean>('','',AttributeType.BooleanAttribute,true,[]);
    public static readonly localDateAttributeMetadata: AttributeMetadata<Date>= new AttributeMetadata<Date>('','',AttributeType.LocalDateAttribute,false,[]);
    public static readonly enumAttributeMetadata: AttributeMetadata<ExampleEnum>= new AttributeMetadata<ExampleEnum>('','',AttributeType.EnumAttribute,false,['VALUE1','VALUE2']);
    public static readonly charAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.CharAttribute,false,[]);
    public static readonly longAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.LongAttribute,false,[]);
    public static readonly stringAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.StringAttribute,false,[]);
    public static readonly integerAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.IntegerAttribute,false,[]);
    public static readonly localDateTimeAttributeMetadata: AttributeMetadata<Date>= new AttributeMetadata<Date>('','',AttributeType.LocalDateTimeAttribute,false,[]);
    public static readonly localeAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.LocaleAttribute,false,[]);
    public static readonly durationAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.DurationAttribute,false,[]);
    public static readonly fileContentAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.FileContentAttribute,false,[]);
    public static readonly localTimeAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.LocalTimeAttribute,false,[]);
    public static readonly shortAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.ShortAttribute,false,[]);
    public static readonly passwordAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.PasswordAttribute,false,[]);
    public static readonly uriAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.URIAttribute,false,[]);
    public static readonly bigDecimalAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.BigDecimalAttribute,false,[]);
    public static readonly floatAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.FloatAttribute,false,[]);
    public static readonly stringListAttributeMetadata: AttributeMetadata<string[]>= new AttributeMetadata<string[]>('','',AttributeType.StringListAttribute,false,[]);
    public static readonly enumListAttributeMetadata: AttributeMetadata<ExampleEnum[]>= new AttributeMetadata<ExampleEnum[]>('','',AttributeType.EnumListAttribute,false,['VALUE1','VALUE2']);
    public static readonly instantAttributeMetadata: AttributeMetadata<Date>= new AttributeMetadata<Date>('','',AttributeType.InstantAttribute,false,[]);
    public static readonly bigIntegerAttributeMetadata: AttributeMetadata<bigint>= new AttributeMetadata<bigint>('','',AttributeType.BigIntegerAttribute,false,[]);
    public static readonly factoryPolymorphicAttributeMetadata: AttributeMetadata<Data>= new AttributeMetadata<Data>('','',AttributeType.FactoryPolymorphicAttribute,false,[]);

    public byteArrayAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.byteArrayAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"byteArrayAttribute"),"byteArrayAttribute");
    }

    public i18nAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.i18nAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"i18nAttribute"),"i18nAttribute");
    }

    public encryptedStringAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.encryptedStringAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"encryptedStringAttribute"),"encryptedStringAttribute");
    }

    public doubleAttributeAccessor(): AttributeAccessor<number>{
        return new AttributeAccessor<number>(ExampleDataAllGenerated.doubleAttributeMetadata,new StaticAttributeValueAccessor<number>(this,"doubleAttribute"),"doubleAttribute");
    }

    public byteAttributeAccessor(): AttributeAccessor<number>{
        return new AttributeAccessor<number>(ExampleDataAllGenerated.byteAttributeMetadata,new StaticAttributeValueAccessor<number>(this,"byteAttribute"),"byteAttribute");
    }

    public booleanAttributeAccessor(): AttributeAccessor<boolean>{
        return new AttributeAccessor<boolean>(ExampleDataAllGenerated.booleanAttributeMetadata,new StaticAttributeValueAccessor<boolean>(this,"booleanAttribute"),"booleanAttribute");
    }

    public localDateAttributeAccessor(): AttributeAccessor<Date>{
        return new AttributeAccessor<Date>(ExampleDataAllGenerated.localDateAttributeMetadata,new StaticAttributeValueAccessor<Date>(this,"localDateAttribute"),"localDateAttribute");
    }

    public enumAttributeAccessor(): AttributeAccessor<ExampleEnum>{
        return new AttributeAccessor<ExampleEnum>(ExampleDataAllGenerated.enumAttributeMetadata,new StaticAttributeValueAccessor<ExampleEnum>(this,"enumAttribute"),"enumAttribute");
    }

    public charAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.charAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"charAttribute"),"charAttribute");
    }

    public longAttributeAccessor(): AttributeAccessor<number>{
        return new AttributeAccessor<number>(ExampleDataAllGenerated.longAttributeMetadata,new StaticAttributeValueAccessor<number>(this,"longAttribute"),"longAttribute");
    }

    public stringAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.stringAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"stringAttribute"),"stringAttribute");
    }

    public integerAttributeAccessor(): AttributeAccessor<number>{
        return new AttributeAccessor<number>(ExampleDataAllGenerated.integerAttributeMetadata,new StaticAttributeValueAccessor<number>(this,"integerAttribute"),"integerAttribute");
    }

    public localDateTimeAttributeAccessor(): AttributeAccessor<Date>{
        return new AttributeAccessor<Date>(ExampleDataAllGenerated.localDateTimeAttributeMetadata,new StaticAttributeValueAccessor<Date>(this,"localDateTimeAttribute"),"localDateTimeAttribute");
    }

    public localeAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.localeAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"localeAttribute"),"localeAttribute");
    }

    public durationAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.durationAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"durationAttribute"),"durationAttribute");
    }

    public fileContentAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.fileContentAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"fileContentAttribute"),"fileContentAttribute");
    }

    public localTimeAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.localTimeAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"localTimeAttribute"),"localTimeAttribute");
    }

    public shortAttributeAccessor(): AttributeAccessor<number>{
        return new AttributeAccessor<number>(ExampleDataAllGenerated.shortAttributeMetadata,new StaticAttributeValueAccessor<number>(this,"shortAttribute"),"shortAttribute");
    }

    public passwordAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.passwordAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"passwordAttribute"),"passwordAttribute");
    }

    public uriAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.uriAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"uriAttribute"),"uriAttribute");
    }

    public bigDecimalAttributeAccessor(): AttributeAccessor<string>{
        return new AttributeAccessor<string>(ExampleDataAllGenerated.bigDecimalAttributeMetadata,new StaticAttributeValueAccessor<string>(this,"bigDecimalAttribute"),"bigDecimalAttribute");
    }

    public floatAttributeAccessor(): AttributeAccessor<number>{
        return new AttributeAccessor<number>(ExampleDataAllGenerated.floatAttributeMetadata,new StaticAttributeValueAccessor<number>(this,"floatAttribute"),"floatAttribute");
    }

    public stringListAttributeAccessor(): AttributeAccessor<string[]>{
        return new AttributeAccessor<string[]>(ExampleDataAllGenerated.stringListAttributeMetadata,new StaticAttributeValueAccessor<string[]>(this,"stringListAttribute"),"stringListAttribute");
    }

    public enumListAttributeAccessor(): AttributeAccessor<ExampleEnum[]>{
        return new AttributeAccessor<ExampleEnum[]>(ExampleDataAllGenerated.enumListAttributeMetadata,new StaticAttributeValueAccessor<ExampleEnum[]>(this,"enumListAttribute"),"enumListAttribute");
    }

    public instantAttributeAccessor(): AttributeAccessor<Date>{
        return new AttributeAccessor<Date>(ExampleDataAllGenerated.instantAttributeMetadata,new StaticAttributeValueAccessor<Date>(this,"instantAttribute"),"instantAttribute");
    }

    public bigIntegerAttributeAccessor(): AttributeAccessor<bigint>{
        return new AttributeAccessor<bigint>(ExampleDataAllGenerated.bigIntegerAttributeMetadata,new StaticAttributeValueAccessor<bigint>(this,"bigIntegerAttribute"),"bigIntegerAttribute");
    }

    public factoryPolymorphicAttributeAccessor(): AttributeAccessor<Data>{
        return new AttributeAccessor<Data>(ExampleDataAllGenerated.factoryPolymorphicAttributeMetadata,new StaticAttributeValueAccessor<Data>(this,"factoryPolymorphicAttribute"),"factoryPolymorphicAttribute");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator, dynamicDataDictionary: DynamicDataDictionary){
        this.byteArrayAttribute=json.byteArrayAttribute.v;
        this.i18nAttribute=json.i18nAttribute.v;
        this.encryptedStringAttribute=json.encryptedStringAttribute.v;
        this.doubleAttribute=json.doubleAttribute.v;
        this.byteAttribute=json.byteAttribute.v;
        this.booleanAttribute=json.booleanAttribute.v;
        this.localDateAttribute=this.mapLocalDateFromJson(json.localDateAttribute.v);
        this.enumAttribute=ExampleEnum.fromJson(json.enumAttribute.v);
        this.charAttribute=json.charAttribute.v;
        this.longAttribute=json.longAttribute.v;
        this.stringAttribute=json.stringAttribute.v;
        this.integerAttribute=json.integerAttribute.v;
        this.localDateTimeAttribute=this.mapLocalDateTimeFromJson(json.localDateTimeAttribute.v);
        this.localeAttribute=json.localeAttribute.v;
        this.durationAttribute=json.durationAttribute.v;
        this.fileContentAttribute=json.fileContentAttribute.v;
        this.localTimeAttribute=json.localTimeAttribute.v;
        this.shortAttribute=json.shortAttribute.v;
        this.passwordAttribute=json.passwordAttribute.v;
        this.uriAttribute=json.uriAttribute.v;
        this.bigDecimalAttribute=json.bigDecimalAttribute.v;
        this.floatAttribute=json.floatAttribute.v;
        this.stringListAttribute=json.stringListAttribute;
        this.enumListAttribute=json.enumListAttribute;
        this.instantAttribute=this.mapInstantFromJson(json.instantAttribute.v);
        this.bigIntegerAttribute=json.bigIntegerAttribute.v;
        this.factoryPolymorphicAttribute=<Data>dataCreator.createData(json.factoryPolymorphicAttribute.v,idToDataMap,this);
    }

    protected mapValuesToJson(idToDataMap: any, result: any){
        result.byteArrayAttribute=this.mapAttributeValueToJson(this.byteArrayAttribute);
        result.i18nAttribute=this.mapAttributeValueToJson(this.i18nAttribute);
        result.encryptedStringAttribute=this.mapAttributeValueToJson(this.encryptedStringAttribute);
        result.doubleAttribute=this.mapAttributeValueToJson(this.doubleAttribute);
        result.byteAttribute=this.mapAttributeValueToJson(this.byteAttribute);
        result.booleanAttribute=this.mapAttributeValueToJson(this.booleanAttribute);
        result.localDateAttribute=this.mapAttributeValueToJson(this.mapLocalDateToJson(this.localDateAttribute));
        result.enumAttribute=this.mapAttributeValueToJson(ExampleEnum.toJson(this.enumAttribute));
        result.charAttribute=this.mapAttributeValueToJson(this.charAttribute);
        result.longAttribute=this.mapAttributeValueToJson(this.longAttribute);
        result.stringAttribute=this.mapAttributeValueToJson(this.stringAttribute);
        result.integerAttribute=this.mapAttributeValueToJson(this.integerAttribute);
        result.localDateTimeAttribute=this.mapAttributeValueToJson(this.mapLocalDateTimeToJson(this.localDateTimeAttribute));
        result.localeAttribute=this.mapAttributeValueToJson(this.localeAttribute);
        result.durationAttribute=this.mapAttributeValueToJson(this.durationAttribute);
        result.fileContentAttribute=this.mapAttributeValueToJson(this.fileContentAttribute);
        result.localTimeAttribute=this.mapAttributeValueToJson(this.localTimeAttribute);
        result.shortAttribute=this.mapAttributeValueToJson(this.shortAttribute);
        result.passwordAttribute=this.mapAttributeValueToJson(this.passwordAttribute);
        result.uriAttribute=this.mapAttributeValueToJson(this.uriAttribute);
        result.bigDecimalAttribute=this.mapAttributeValueToJson(this.bigDecimalAttribute);
        result.floatAttribute=this.mapAttributeValueToJson(this.floatAttribute);
        result.stringListAttribute=this.stringListAttribute;
        result.enumListAttribute=this.enumListAttribute;
        result.instantAttribute=this.mapAttributeValueToJson(this.mapInstantToJson(this.instantAttribute));
        result.bigIntegerAttribute=this.mapAttributeValueToJson(this.bigIntegerAttribute);
        result.factoryPolymorphicAttribute=this.mapAttributeDataToJson(idToDataMap,this.factoryPolymorphicAttribute);
    }

    protected collectChildrenFlat(): Data[]{
        let result: Array<Data>=[];
        if (this.factoryPolymorphicAttribute) result.push(this.factoryPolymorphicAttribute);
        return result;
    }

    public listAttributeAccessor(): AttributeAccessor<any>[]{
        let result: AttributeAccessor<any>[]=[];
        result.push(this.byteArrayAttributeAccessor());
        result.push(this.i18nAttributeAccessor());
        result.push(this.encryptedStringAttributeAccessor());
        result.push(this.doubleAttributeAccessor());
        result.push(this.byteAttributeAccessor());
        result.push(this.booleanAttributeAccessor());
        result.push(this.localDateAttributeAccessor());
        result.push(this.enumAttributeAccessor());
        result.push(this.charAttributeAccessor());
        result.push(this.longAttributeAccessor());
        result.push(this.stringAttributeAccessor());
        result.push(this.integerAttributeAccessor());
        result.push(this.localDateTimeAttributeAccessor());
        result.push(this.localeAttributeAccessor());
        result.push(this.durationAttributeAccessor());
        result.push(this.fileContentAttributeAccessor());
        result.push(this.localTimeAttributeAccessor());
        result.push(this.shortAttributeAccessor());
        result.push(this.passwordAttributeAccessor());
        result.push(this.uriAttributeAccessor());
        result.push(this.bigDecimalAttributeAccessor());
        result.push(this.floatAttributeAccessor());
        result.push(this.stringListAttributeAccessor());
        result.push(this.enumListAttributeAccessor());
        result.push(this.instantAttributeAccessor());
        result.push(this.bigIntegerAttributeAccessor());
        result.push(this.factoryPolymorphicAttributeAccessor());
        return result;
    }

}