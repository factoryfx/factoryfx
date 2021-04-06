package io.github.factoryfx.factory.record;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecordDeserializer extends JsonDeserializer<RecordFactory<?,?,?>> {

    @Override
    public RecordFactory<?,?,?> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
//        int id = (Integer) ((IntNode) node.get("id")).numberValue();
//        String itemName = node.get("itemName").asText();
//        int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();

        String classRecord = node.get("@classRecord").asText();


        try {
            Class<?> dependenciesClass = Class.forName(classRecord);


            var types = new ArrayList<Class<?>>();
            var values = new ArrayList<>();
            for (var component : dependenciesClass.getRecordComponents()) {
                types.add(component.getType());

                Object value=null;
                if (node.get(component.getName())!=null){
//                    ctxt.jp.readValueAs(User.class);

                    if (List.class.isAssignableFrom(component.getType())){
//                        var listItemType = (Class<?>) ((ParameterizedType) component.getType()
//                                .getGenericSuperclass()).getActualTypeArguments()[0];
                        DependencyList dependencyList = new DependencyList(null);
                        dependencyList.addAll(ObjectMapperBuilder.build().treeToValueList(node.get(component.getName()), RecordFactory.class));
                        value = dependencyList;
                    } else {

                        Class<?> type = component.getType();
                        if (type == Dependency.class){
                            value = ((RecordFactory)ObjectMapperBuilder.build().treeToValue(node.get(component.getName()).get("v"), RecordFactory.class)).dep();
                        } else {
                            value = ObjectMapperBuilder.build().treeToValue(node.get(component.getName()).get("v"), component.getType());
                        }

                    }



                    //value = ctxt.readValue(node.get(component.getName()).get("v").traverse(), component.getType());
//
//                            jp.readValueAs(User.class);
//
//                            node.get(component.getName()).get("v").readValueAs(User.class)
                }
                values.add(value);
            }

            System.out.println(types);
            System.out.println(dependenciesClass);
            System.out.println(Arrays.stream(dependenciesClass.getDeclaredConstructors()).map(c->Arrays.toString(c.getParameterTypes())).collect(Collectors.toList()));
            Constructor<?> constructor = dependenciesClass.getDeclaredConstructor(types.toArray(Class[]::new));
            return new RecordFactory((Dependencies) constructor.newInstance(values.toArray(Object[]::new)));

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
