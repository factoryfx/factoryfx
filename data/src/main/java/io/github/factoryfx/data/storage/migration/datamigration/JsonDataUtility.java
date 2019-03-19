package io.github.factoryfx.data.storage.migration.datamigration;

public class JsonDataUtility {

//    public List<DataJsonNode> readDataList(JsonNode jsonNode){
//        ArrayList<DataJsonNode> result = new ArrayList<>();
//        readDataList(jsonNode, result);
//        return result;
//
//    }
//
//    private void readDataList(JsonNode jsonNode, List<DataJsonNode> result){
//        if (isData(jsonNode)) {
//            result.add(new DataJsonNode((ObjectNode)jsonNode));
//        }
//        for (JsonNode element : jsonNode) {
//            if (element.isArray()) {
//                for (JsonNode arrayElement : element) {
//                    readDataList(arrayElement, result);
//                }
//            } else {
//                readDataList(element, result);
//            }
//        }
//
//    }
//
//    private boolean isData(JsonNode jsonNode){
//        if (jsonNode.fieldNames().hasNext()){
//            String fieldName = jsonNode.fieldNames().next();
//            return "@class".equals(fieldName);
//        }
//        return false;
//    }

}
