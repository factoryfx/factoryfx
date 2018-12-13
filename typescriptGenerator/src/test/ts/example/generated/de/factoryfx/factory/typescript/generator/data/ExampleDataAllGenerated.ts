//generated code don't edit manually
import { AttributeAccessor } from "../../../../../../../util/AttributeAccessor";
import { DataCreator } from "../../../../../../../util/DataCreator";
import { AttributeType } from "../../../../../../../util/AttributeType";
import { Data } from "../../../../../../../util/Data";
import { ExampleEnum } from "./ExampleEnum";
import { AttributeMetadata } from "../../../../../../../util/AttributeMetadata";

export abstract class ExampleDataAllGenerated  extends Data {

    public byteArrayAttribute: string;
    public i18nAttribute: string;
    public encryptedStringAttribute: string;
    public doubleAttribute: number;
    public byteAttribute: number;
    public booleanAttribute: boolean;
    public localDateAttribute: Date;
    public enumAttribute: ExampleEnum;
    public charAttribute: string;
    public longAttribute: number;
    public stringAttribute: string;
    public integerAttribute: number;
    public localDateTimeAttribute: Date;
    public localeAttribute: string;
    public durationAttribute: string;
    public fileContentAttribute: string;
    public localTimeAttribute: string;
    public shortAttribute: number;
    public passwordAttribute: string;
    public uriAttribute: string;
    public bigDecimalAttribute: string;
    public floatAttribute: number;
    public stringListAttribute: string[];
    public enumListAttribute: ExampleEnum[];
    public instantAttribute: Date;
    public bigIntegerAttribute: bigint;
    public static readonly byteArrayAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.ByteArrayAttribute);
    public static readonly i18nAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.I18nAttribute);
    public static readonly encryptedStringAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.EncryptedStringAttribute);
    public static readonly doubleAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.DoubleAttribute);
    public static readonly byteAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.ByteAttribute);
    public static readonly booleanAttributeMetadata: AttributeMetadata<boolean>= new AttributeMetadata<boolean>('','',AttributeType.BooleanAttribute);
    public static readonly localDateAttributeMetadata: AttributeMetadata<Date>= new AttributeMetadata<Date>('','',AttributeType.LocalDateAttribute);
    public static readonly enumAttributeMetadata: AttributeMetadata<ExampleEnum>= new AttributeMetadata<ExampleEnum>('','',AttributeType.EnumAttribute);
    public static readonly charAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.CharAttribute);
    public static readonly longAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.LongAttribute);
    public static readonly stringAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.StringAttribute);
    public static readonly integerAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.IntegerAttribute);
    public static readonly localDateTimeAttributeMetadata: AttributeMetadata<Date>= new AttributeMetadata<Date>('','',AttributeType.LocalDateTimeAttribute);
    public static readonly localeAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.LocaleAttribute);
    public static readonly durationAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.DurationAttribute);
    public static readonly fileContentAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.FileContentAttribute);
    public static readonly localTimeAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.LocalTimeAttribute);
    public static readonly shortAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.ShortAttribute);
    public static readonly passwordAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.PasswordAttribute);
    public static readonly uriAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.URIAttribute);
    public static readonly bigDecimalAttributeMetadata: AttributeMetadata<string>= new AttributeMetadata<string>('','',AttributeType.BigDecimalAttribute);
    public static readonly floatAttributeMetadata: AttributeMetadata<number>= new AttributeMetadata<number>('','',AttributeType.FloatAttribute);
    public static readonly stringListAttributeMetadata: AttributeMetadata<string[]>= new AttributeMetadata<string[]>('','',AttributeType.StringListAttribute);
    public static readonly enumListAttributeMetadata: AttributeMetadata<ExampleEnum[]>= new AttributeMetadata<ExampleEnum[]>('','',AttributeType.EnumListAttribute);
    public static readonly instantAttributeMetadata: AttributeMetadata<Date>= new AttributeMetadata<Date>('','',AttributeType.InstantAttribute);
    public static readonly bigIntegerAttributeMetadata: AttributeMetadata<bigint>= new AttributeMetadata<bigint>('','',AttributeType.BigIntegerAttribute);

    public byteArrayAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.byteArrayAttributeMetadata,this,"byteArrayAttribute");
    }

    public i18nAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.i18nAttributeMetadata,this,"i18nAttribute");
    }

    public encryptedStringAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.encryptedStringAttributeMetadata,this,"encryptedStringAttribute");
    }

    public doubleAttributeAccessor(): AttributeAccessor<number,ExampleDataAllGenerated>{
        return new AttributeAccessor<number,ExampleDataAllGenerated>(ExampleDataAllGenerated.doubleAttributeMetadata,this,"doubleAttribute");
    }

    public byteAttributeAccessor(): AttributeAccessor<number,ExampleDataAllGenerated>{
        return new AttributeAccessor<number,ExampleDataAllGenerated>(ExampleDataAllGenerated.byteAttributeMetadata,this,"byteAttribute");
    }

    public booleanAttributeAccessor(): AttributeAccessor<boolean,ExampleDataAllGenerated>{
        return new AttributeAccessor<boolean,ExampleDataAllGenerated>(ExampleDataAllGenerated.booleanAttributeMetadata,this,"booleanAttribute");
    }

    public localDateAttributeAccessor(): AttributeAccessor<Date,ExampleDataAllGenerated>{
        return new AttributeAccessor<Date,ExampleDataAllGenerated>(ExampleDataAllGenerated.localDateAttributeMetadata,this,"localDateAttribute");
    }

    public enumAttributeAccessor(): AttributeAccessor<ExampleEnum,ExampleDataAllGenerated>{
        return new AttributeAccessor<ExampleEnum,ExampleDataAllGenerated>(ExampleDataAllGenerated.enumAttributeMetadata,this,"enumAttribute");
    }

    public charAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.charAttributeMetadata,this,"charAttribute");
    }

    public longAttributeAccessor(): AttributeAccessor<number,ExampleDataAllGenerated>{
        return new AttributeAccessor<number,ExampleDataAllGenerated>(ExampleDataAllGenerated.longAttributeMetadata,this,"longAttribute");
    }

    public stringAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.stringAttributeMetadata,this,"stringAttribute");
    }

    public integerAttributeAccessor(): AttributeAccessor<number,ExampleDataAllGenerated>{
        return new AttributeAccessor<number,ExampleDataAllGenerated>(ExampleDataAllGenerated.integerAttributeMetadata,this,"integerAttribute");
    }

    public localDateTimeAttributeAccessor(): AttributeAccessor<Date,ExampleDataAllGenerated>{
        return new AttributeAccessor<Date,ExampleDataAllGenerated>(ExampleDataAllGenerated.localDateTimeAttributeMetadata,this,"localDateTimeAttribute");
    }

    public localeAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.localeAttributeMetadata,this,"localeAttribute");
    }

    public durationAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.durationAttributeMetadata,this,"durationAttribute");
    }

    public fileContentAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.fileContentAttributeMetadata,this,"fileContentAttribute");
    }

    public localTimeAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.localTimeAttributeMetadata,this,"localTimeAttribute");
    }

    public shortAttributeAccessor(): AttributeAccessor<number,ExampleDataAllGenerated>{
        return new AttributeAccessor<number,ExampleDataAllGenerated>(ExampleDataAllGenerated.shortAttributeMetadata,this,"shortAttribute");
    }

    public passwordAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.passwordAttributeMetadata,this,"passwordAttribute");
    }

    public uriAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.uriAttributeMetadata,this,"uriAttribute");
    }

    public bigDecimalAttributeAccessor(): AttributeAccessor<string,ExampleDataAllGenerated>{
        return new AttributeAccessor<string,ExampleDataAllGenerated>(ExampleDataAllGenerated.bigDecimalAttributeMetadata,this,"bigDecimalAttribute");
    }

    public floatAttributeAccessor(): AttributeAccessor<number,ExampleDataAllGenerated>{
        return new AttributeAccessor<number,ExampleDataAllGenerated>(ExampleDataAllGenerated.floatAttributeMetadata,this,"floatAttribute");
    }

    public stringListAttributeAccessor(): AttributeAccessor<string[],ExampleDataAllGenerated>{
        return new AttributeAccessor<string[],ExampleDataAllGenerated>(ExampleDataAllGenerated.stringListAttributeMetadata,this,"stringListAttribute");
    }

    public enumListAttributeAccessor(): AttributeAccessor<ExampleEnum[],ExampleDataAllGenerated>{
        return new AttributeAccessor<ExampleEnum[],ExampleDataAllGenerated>(ExampleDataAllGenerated.enumListAttributeMetadata,this,"enumListAttribute");
    }

    public instantAttributeAccessor(): AttributeAccessor<Date,ExampleDataAllGenerated>{
        return new AttributeAccessor<Date,ExampleDataAllGenerated>(ExampleDataAllGenerated.instantAttributeMetadata,this,"instantAttribute");
    }

    public bigIntegerAttributeAccessor(): AttributeAccessor<bigint,ExampleDataAllGenerated>{
        return new AttributeAccessor<bigint,ExampleDataAllGenerated>(ExampleDataAllGenerated.bigIntegerAttributeMetadata,this,"bigIntegerAttribute");
    }

    protected mapValuesFromJson(json: any, idToDataMap: any, dataCreator: DataCreator){
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
    }

    protected collectChildrenRecursiveIntern(idToDataMap: any){
        
    }

    protected listAttributeAccessor(): AttributeAccessor<any,ExampleDataAllGenerated>[]{
        let result: AttributeAccessor<any,ExampleDataAllGenerated>[]=[];
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
        return result;
    }

}