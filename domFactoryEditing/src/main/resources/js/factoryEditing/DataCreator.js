export class DataCreator {
    createData(json, idToDataMap, parent) {
        if (!json)
            return null;
        let clazz = json['@class'];
        if (typeof json === 'string') {
            return idToDataMap[json];
        }
        return null;
    }
    createDataList(json, idToDataMap, parent) {
        let result = [];
        for (let entry of json) {
            result.push(this.createData(entry, idToDataMap, parent));
        }
        return result;
    }
}
