package io.github.factoryfx.factory.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * <a href="https://github.com/FasterXML/jackson-databind/issues/796">...</a>
 * don't implicit cast non String value like true/false,1 to string
 *
 */
public class ForceStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT ||
                jsonParser.getCurrentToken() == JsonToken.VALUE_FALSE ||
                jsonParser.getCurrentToken() == JsonToken.VALUE_TRUE ||
                jsonParser.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT
        ) {
            throw deserializationContext.wrongTokenException(jsonParser, String.class, JsonToken.VALUE_STRING, "can't deserialize non string value to string");
        }
        return jsonParser.getValueAsString();
    }
}