export class Data {
    getId() {
        if (!this.id) {
            this.id = this.uuidV4();
        }
        return this.id;
    }
    uuidV4() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
            let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }
    //DataCreator passed as parameter cause cyclic dependency (ts compiles fine but error at runtime)
    mapFromJsonFromRoot(json, dataCreator) {
        this.mapFromJson(json, {}, dataCreator, null);
    }
    mapFromJsonFromRootDynamic(json, dynamicDataDictionary) {
        this.mapFromJson(json, {}, null, dynamicDataDictionary);
    }
    mapFromJsonFromRootDynamicWidthMap(json, idToDataMap, dynamicDataDictionary) {
        this.mapFromJson(json, idToDataMap, null, dynamicDataDictionary);
    }
    mapFromJson(json, idToDataMap, dataCreator, dynamicDataDictionary) {
        this.id = json.id;
        this.javaClass = json['@class'];
        this.mapValuesFromJson(json, idToDataMap, dataCreator, dynamicDataDictionary);
        idToDataMap[this.id] = this;
    }
    mapToJsonFromRoot() {
        return this.mapToJson({});
    }
    mapToJson(idToDataMap) {
        if (idToDataMap[this.id]) {
            return this.id;
        }
        idToDataMap[this.id] = this;
        let result = {};
        result['@class'] = this.javaClass;
        result.id = this.id;
        this.mapValuesToJson(idToDataMap, result);
        return result;
    }
    mapAttributeValueToJson(value) {
        if (value !== null && value !== undefined) {
            return {
                v: value
            };
        }
        return {};
    }
    mapAttributeDataToJson(idToDataMap, data) {
        if (data) {
            return {
                v: data.mapToJson(idToDataMap)
            };
        }
        return {};
    }
    mapAttributeDataListToJson(idToDataMap, dataList) {
        let result = [];
        if (dataList) {
            for (let entry of dataList) {
                result.push(entry.mapToJson(idToDataMap));
            }
        }
        return result;
    }
    collectChildrenRecursive(idToDataMap) {
        if (this && !idToDataMap[this.getId()]) {
            idToDataMap[this.getId()] = this;
        }
        else {
            return;
        }
        for (let child of this.collectChildrenFlat()) {
            child.collectChildrenRecursive(idToDataMap);
        }
    }
    getChildrenFlat() {
        return this.collectChildrenFlat();
    }
    collectChildren() {
        let idToDataMap = {};
        this.collectChildrenRecursive(idToDataMap);
        let result = [];
        for (let child in idToDataMap)
            result.push(idToDataMap[child]);
        //Object["values"](idToDataMap);//Object.values(idToDataMap);
        return result;
    }
    pad(num, size) {
        let s = num + "";
        while (s.length < size)
            s = "0" + s;
        return s;
    }
    mapLocalDateFromJson(json) {
        return new Date(json);
    }
    mapLocalDateToJson(date) {
        let day = date.getDate();
        let monthIndex = date.getMonth() + 1;
        let year = date.getFullYear();
        return year + "-" + this.pad(monthIndex, 2) + "-" + this.pad(day, 2);
    }
    mapInstantFromJson(json) {
        let match = /(.*)\.(.*)Z/.exec(json);
        let convertNanoToMilli = match[1] + '.' + this.pad((Math.round(Number(match[2]) / 100000)), 2) + 'Z';
        let current = new Date(convertNanoToMilli);
        let utcDate = new Date(current.getTime() + current.getTimezoneOffset() * 60000);
        return utcDate;
    }
    mapInstantToJson(date) {
        let day = date.getDate();
        let monthIndex = date.getMonth() + 1;
        let year = date.getFullYear();
        let hour = date.getHours();
        let min = date.getMinutes();
        let sec = date.getSeconds();
        let milliseconds = date.getMilliseconds();
        return year + "-" + this.pad(monthIndex, 2) + "-" + this.pad(day, 2) + 'T' + this.pad(hour, 2) + ':' + this.pad(min, 2) + ':' + this.pad(sec, 2) + '.' + milliseconds + 'Z';
    }
    mapLocalDateTimeFromJson(json) {
        return new Date(json);
    }
    mapLocalDateTimeToJson(date) {
        let day = date.getDate();
        let monthIndex = date.getMonth() + 1;
        let year = date.getFullYear();
        let hour = date.getHours();
        let min = date.getMinutes();
        let sec = date.getSeconds();
        let milliseconds = date.getMilliseconds();
        return year + "-" + this.pad(monthIndex, 2) + "-" + this.pad(day, 2) + 'T' + this.pad(hour, 2) + ':' + this.pad(min, 2) + ':' + this.pad(sec, 2) + '.' + milliseconds;
    }
    getPath() {
        let result = [];
        let data = this;
        while (data) {
            result.push(data);
            data = data.parent;
        }
        result.reverse();
        return result;
    }
    getRoot() {
        return this.getPath()[0];
    }
    setParent(parent) {
        this.parent = parent;
    }
    addBackReferences() {
        let stack = [];
        stack.push(this);
        let data;
        do {
            data = stack.pop();
            if (data) {
                for (let child of data.collectChildrenFlat()) {
                    child.setParent(data);
                    stack.push(child);
                }
            }
        } while (data);
    }
    getParent() {
        return this.parent;
    }
    getJavaClass() {
        return this.javaClass;
    }
    getChildFromRoot(factoryId) {
        let idToDataMap = {};
        this.collectChildrenRecursive(idToDataMap);
        return idToDataMap[factoryId];
    }
    addChangeListener(listener) {
        for (let attribute of this.listAttributeAccessor()) {
            attribute.addChangeListener(listener);
        }
    }
    removeChangeListener(listener) {
        for (let attribute of this.listAttributeAccessor()) {
            attribute.removeChangeListener(listener);
        }
    }
}
