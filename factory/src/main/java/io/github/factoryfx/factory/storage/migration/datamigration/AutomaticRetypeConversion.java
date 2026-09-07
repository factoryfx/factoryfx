package io.github.factoryfx.factory.storage.migration.datamigration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.github.factoryfx.factory.storage.migration.metadata.AttributeStorageMetadata;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * automatic value conversion for retyped attributes.<br>
 * only lossless/obvious conversions between the built-in scalar attribute types (and their list/set variants) are performed,
 * everything else results in null which matches the previous behaviour (retyped attributes were always cleared).
 * lossy cases (e.g. list with more than one element to a single value attribute, unparseable string to number) result in null and a warning.
 */
public class AutomaticRetypeConversion {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AutomaticRetypeConversion.class);

    private enum Kind {STRING, INTEGRAL, DECIMAL, BOOLEAN}

    private static final Map<String, Kind> ATTRIBUTE_CLASS_TO_KIND = new HashMap<>();
    static {
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.types.StringAttribute", Kind.STRING);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.types.StringListAttribute", Kind.STRING);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.types.StringSetAttribute", Kind.STRING);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.IntegerAttribute", Kind.INTEGRAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.LongAttribute", Kind.INTEGRAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.ShortAttribute", Kind.INTEGRAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.ByteAttribute", Kind.INTEGRAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.types.BigIntegerAttribute", Kind.INTEGRAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.list.IntegerListAttribute", Kind.INTEGRAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.list.LongListAttribute", Kind.INTEGRAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.list.ShortListAttribute", Kind.INTEGRAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.list.ByteListAttribute", Kind.INTEGRAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.DoubleAttribute", Kind.DECIMAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.FloatAttribute", Kind.DECIMAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.types.BigDecimalAttribute", Kind.DECIMAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.list.DoubleListAttribute", Kind.DECIMAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.list.FloatListAttribute", Kind.DECIMAL);
        ATTRIBUTE_CLASS_TO_KIND.put("io.github.factoryfx.factory.attribute.primitive.BooleanAttribute", Kind.BOOLEAN);
    }

    /**
     * convert the stored value of a retyped attribute to the new attribute type if possible
     * @param oldValue unwrapped stored value ({@link DataJsonNode#getAttributeValue(String)}), may be null
     * @param dataClassName fully qualified name of the factory class containing the attribute, used for logging
     * @param attributeStorageMetadata stored attribute metadata marked with the retype target
     * @return converted unwrapped value or null if no automatic conversion is possible
     */
    public static JsonNode convert(JsonNode oldValue, String dataClassName, AttributeStorageMetadata attributeStorageMetadata) {
        if (oldValue == null || oldValue.isNull()) {
            return null;
        }
        Kind targetKind = ATTRIBUTE_CLASS_TO_KIND.get(attributeStorageMetadata.getRetypedToAttributeClassName());
        if (targetKind == null) {
            if (attributeStorageMetadata.getReferenceClass() != null
                    && attributeStorageMetadata.getReferenceClass().equals(attributeStorageMetadata.getRetypedToReferenceClassName())) {
                //reference attribute retyped to another reference attribute with the same reference class
                //(e.g. list attribute replaced with a single reference): the serialized elements are compatible, only the shape changes
                return convertReference(oldValue, attributeStorageMetadata);
            }
            //unknown attribute type or incompatible reference: no automatic conversion
            logger.warn("attribute '{}#{}' was retyped from {} to {}: no automatic conversion, the stored value is cleared. " +
                            "Register MicroserviceBuilder.withRetypeAttributeMigration to convert the value, or withRenameAttributeClassMigration if only the attribute class was renamed/moved",
                    dataClassName, attributeStorageMetadata.getVariableName(), attributeStorageMetadata.getAttributeClassName(), attributeStorageMetadata.getRetypedToAttributeClassName());
            return null;
        }
        boolean targetList = attributeStorageMetadata.isRetypedToList();
        if (oldValue.isArray()) {
            if (targetList) {
                ArrayNode result = JsonNodeFactory.instance.arrayNode();
                for (JsonNode element : oldValue) {
                    JsonNode converted = convertScalar(element, targetKind);
                    if (converted == null) {
                        logger.warn("automatic retype conversion of attribute '{}' skipped: list element {} is not convertible to {}", attributeStorageMetadata.getVariableName(), element, targetKind);
                        return null;
                    }
                    result.add(converted);
                }
                return result;
            }
            if (oldValue.isEmpty()) {
                return null;
            }
            if (oldValue.size() == 1) {
                return convertScalar(oldValue.get(0), targetKind);
            }
            logger.warn("automatic retype conversion of attribute '{}' skipped: list with {} elements can't be converted to a single value", attributeStorageMetadata.getVariableName(), oldValue.size());
            return null;
        }
        JsonNode converted = convertScalar(oldValue, targetKind);
        if (targetList) {
            if (converted == null) {
                return null;
            }
            ArrayNode result = JsonNodeFactory.instance.arrayNode();
            result.add(converted);
            return result;
        }
        return converted;
    }

    private static JsonNode convertReference(JsonNode oldValue, AttributeStorageMetadata attributeStorageMetadata) {
        boolean targetList = attributeStorageMetadata.isRetypedToList();
        if (oldValue.isArray()) {
            if (targetList) {
                return oldValue;//list to list with unchanged reference class (e.g. renamed list attribute class): serialized form is compatible
            }
            ArrayNode array = (ArrayNode) oldValue;
            if (array.isEmpty()) {
                return null;
            }
            if (array.size() > 1) {
                logger.warn("automatic retype conversion of reference attribute '{}': list with {} elements converted to a single reference, the first element is used and the remaining {} are dropped", attributeStorageMetadata.getVariableName(), array.size(), array.size() - 1);
            }
            return array.get(0);
        }
        if (targetList) {
            ArrayNode result = JsonNodeFactory.instance.arrayNode();
            result.add(oldValue);
            return result;
        }
        return oldValue;//single to single with unchanged reference class: serialized form is compatible
    }

    private static JsonNode convertScalar(JsonNode value, Kind targetKind) {
        if (value == null || value.isNull() || !value.isValueNode()) {
            return null;
        }
        switch (targetKind) {
            case STRING:
                return TextNode.valueOf(value.asText());
            case INTEGRAL:
                if (value.isIntegralNumber()) {
                    return value;
                }
                if (value.isTextual()) {
                    try {
                        return LongNode.valueOf(Long.parseLong(value.asText().trim()));
                    } catch (NumberFormatException e) {
                        logger.warn("automatic retype conversion skipped: '{}' is not an integral number", value.asText());
                        return null;
                    }
                }
                return null;
            case DECIMAL:
                if (value.isNumber()) {
                    return value;
                }
                if (value.isTextual()) {
                    try {
                        return DoubleNode.valueOf(Double.parseDouble(value.asText().trim()));
                    } catch (NumberFormatException e) {
                        logger.warn("automatic retype conversion skipped: '{}' is not a number", value.asText());
                        return null;
                    }
                }
                return null;
            case BOOLEAN:
                if (value.isBoolean()) {
                    return value;
                }
                if (value.isTextual()) {
                    String text = value.asText().trim();
                    if ("true".equalsIgnoreCase(text)) {return BooleanNode.TRUE;}
                    if ("false".equalsIgnoreCase(text)) {return BooleanNode.FALSE;}
                }
                return null;
            default:
                return null;
        }
    }
}
