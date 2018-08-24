package de.factoryfx.data.storage.migration;

import java.util.function.Function;

public class UnknownPropertyRemover implements Function<String, String> {
    @Override
    public String apply(String input) {
//        ObjectMapper mapper = ObjectMapperBuilder.buildNewObjectMapper();
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        ServerFactory orig;
//        try {
//            orig = mapper.readValue(input, ServerFactory.class);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            return mapper.writeValueAsString(orig);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }
}

