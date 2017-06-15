package de.factoryfx.javafx.javascript.editor.data;

import java.util.UUID;
import java.util.regex.Pattern;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.DataReferenceAttribute;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.attribute.types.BigDecimalAttribute;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.types.ByteArrayAttribute;
import de.factoryfx.data.attribute.types.ColorAttribute;
import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.LocalDateAttribute;
import de.factoryfx.data.attribute.types.LocalDateTimeAttribute;
import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.data.attribute.primitive.LongAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.data.attribute.types.StringMapAttribute;
import de.factoryfx.data.attribute.types.URIAttribute;
import de.factoryfx.data.attribute.types.URIListAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.RegexValidation;
import de.factoryfx.data.validation.StringRequired;
import de.factoryfx.data.validation.Validation;

public class ExampleData1 extends Data {

    public ExampleData1() {

    }
}
