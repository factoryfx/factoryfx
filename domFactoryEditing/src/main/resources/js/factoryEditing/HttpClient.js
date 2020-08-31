import { StoredDataMetadata } from "./StoredDataMetadata";
import { DynamicDataDictionary } from "./DynamicDataDictionary";
import { DynamicData } from "./DynamicData";
export class HttpClient {
    constructor(statusReporter) {
        this.statusReporter = statusReporter;
    }
    getHistoryFactoryList(responseCallback) {
        let request = {
            "user": "",
            "passwordHash": "",
        };
        this.post("historyFactoryList", request, (response) => {
            let result = [];
            for (let item of response) {
                result.push(new StoredDataMetadata().mapFromJson(item));
            }
            responseCallback(result);
        });
    }
    getUserLocale(responseCallback) {
        let request = {
            "user": "",
            "passwordHash": "",
        };
        this.post("userLocale", request, (response) => {
            responseCallback(response.locale);
        });
    }
    updateCurrentFactory(updateRoot, baseVersionId, comment, responseCallback) {
        let saveRequestBody = {
            "user": "",
            "passwordHash": "",
            "request": {
                "@class": "io.github.factoryfx.factory.storage.DataUpdate",
                "root": updateRoot.mapToJsonFromRoot(),
                "user": "1",
                "comment": comment,
                "baseVersionId": baseVersionId
            }
        };
        this.post("updateCurrentFactory", saveRequestBody, responseCallback);
    }
    prepareNewFactory(responseCallback) {
        this.post("prepareNewFactory", {}, (prepareNewFactoryResponse) => {
            responseCallback(prepareNewFactoryResponse.root, prepareNewFactoryResponse.baseVersionId);
        });
    }
    getMetadata(responseCallback) {
        this.get("metadata", (response) => {
            responseCallback(response.dynamicDataDictionary, response.guiConfiguration);
        });
    }
    createNewFactories(factoryId, attributeVariableName, root, responseCallback) {
        let createRequestBody = {
            "factoryId": factoryId,
            "attributeVariableName": attributeVariableName,
            "root": root.mapToJsonFromRoot()
        };
        this.post("createNewFactories", createRequestBody, (response) => {
            let dynamicDataDictionary = new DynamicDataDictionary();
            dynamicDataDictionary.mapFromJson(response.dynamicDataDictionary);
            let possibleValues = [];
            let idToDataMap = {};
            for (let collectChild of root.collectChildren()) {
                idToDataMap[collectChild.getId()] = collectChild;
            }
            for (let json of response.factories) {
                if (typeof json === 'string') {
                    possibleValues.push(idToDataMap[json]);
                }
                else {
                    let dynamicData = new DynamicData();
                    dynamicData.mapFromJsonFromRootDynamicWidthMap(json, idToDataMap, dynamicDataDictionary);
                    possibleValues.push(dynamicData);
                }
            }
            responseCallback(possibleValues);
        });
    }
    encryptAttribute(text, key, responseCallback) {
        let encryptAttributeRequestBody = {
            "text": text,
            "key": key
        };
        this.post("encryptAttribute", encryptAttributeRequestBody, (response) => {
            responseCallback(response.encryptedText);
        });
    }
    decryptAttribute(encryptedText, key, responseCallback) {
        let encryptAttributeRequestBody = {
            "key": key,
            "encryptedText": encryptedText
        };
        this.post("decryptAttribute", encryptAttributeRequestBody, responseCallback);
    }
    resolveViewRequest(factoryId, attributeVariableName, root, responseCallback) {
        let request = {
            factoryId: factoryId,
            attributeVariableName: attributeVariableName,
            root: root.mapToJsonFromRoot(),
        };
        this.post("resolveViewRequest", request, responseCallback);
    }
    resolveViewList(factoryId, attributeVariableName, root, responseCallback) {
        let request = {
            factoryId: factoryId,
            attributeVariableName: attributeVariableName,
            root: root.mapToJsonFromRoot(),
        };
        this.post("resolveViewList", request, responseCallback);
    }
    post(url, requestBody, responseCallback) {
        let request = new XMLHttpRequest();
        request.open("POST", url);
        request.setRequestHeader("Content-type", "application/json");
        request.onload = (e) => {
            if (request.status >= 200 && request.status < 300) {
                responseCallback(JSON.parse(request.responseText));
                this.statusReporter.hideWaitAnimation();
            }
            else {
                this.statusReporter.reportError(request.responseText);
            }
        };
        this.statusReporter.showWaitAnimation();
        request.send(JSON.stringify(requestBody));
    }
    get(url, responseCallback) {
        let request = new XMLHttpRequest();
        request.open("GET", url);
        request.setRequestHeader("Content-type", "application/json");
        request.onload = (e) => {
            if (request.status >= 200 && request.status < 300) {
                responseCallback(JSON.parse(request.responseText));
                this.statusReporter.hideWaitAnimation();
            }
            else {
                this.statusReporter.reportError(request.responseText);
            }
        };
        this.statusReporter.showWaitAnimation();
        request.send();
    }
}
