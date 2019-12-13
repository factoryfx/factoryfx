import {Data} from "./Data";
import {HttpClientStatusReporter} from "./HttpClientStatusReporter";
import {StoredDataMetadata} from "./StoredDataMetadata";


export class HttpClient {

    constructor(private statusReporter : HttpClientStatusReporter){

    }

    getHistoryFactoryList(responseCallback: ((storedDataMetadataList: StoredDataMetadata[]) => any)){
        let request = {
            "user": "",
            "passwordHash": "",
        };
        this.post("historyFactoryList",request,(response)=>{
            let result:StoredDataMetadata[]= [];
            for (let item of response){
                result.push(new StoredDataMetadata().mapFromJson(item))
            }
            responseCallback(result);
        });
    }

    getUserLocale(responseCallback: ((locale: string) => any)){
        let request = {
            "user": "",
            "passwordHash": "",
        };
        this.post("userLocale",request,(response)=>{
            responseCallback(response.locale);
        });
    }

    updateCurrentFactory(updateRoot: Data, baseVersionId: string, comment: string, responseCallback: ((response: any) => any)){
        let saveRequestBody = {
            "user": "",
            "passwordHash": "",
            "request": {
                "@class": "io.github.factoryfx.factory.storage.DataUpdate",
                "root":updateRoot.mapToJsonFromRoot(),
                "user": "1",
                "comment": comment,
                "baseVersionId": baseVersionId
            }
        };
        this.post("updateCurrentFactory",saveRequestBody,responseCallback);
    }


    prepareNewFactory(responseCallback: ((root: any, baseVersionId: string) => any)){
        this.post("prepareNewFactory",{},(prepareNewFactoryResponse: any)=> {
            responseCallback(prepareNewFactoryResponse.root,prepareNewFactoryResponse.baseVersionId);
        });
    }

    getMetadata(responseCallback: ((dynamicDataDictionaryJson: any, guiConfiguration: any) => any)){
        this.get("metadata",(response)=>{
            responseCallback(response.dynamicDataDictionary,response.guiConfiguration);
        });
    }

    createNewFactory(factoryId: string, attributeVariableName: string, root: Data, responseCallback: ((response: any) => any)){
        let createRequestBody: any = {
            "factoryId" : factoryId,
            "attributeVariableName" : attributeVariableName,
            "root" : root.mapToJsonFromRoot()
        };
        this.post("createNewFactory",createRequestBody,responseCallback);
    }

    encryptAttribute(text: string, key: string, responseCallback: (encryptedText: string)=>any){
        let encryptAttributeRequestBody = {
            "text" : text,
            "key" : key
        };
        this.post("encryptAttribute",encryptAttributeRequestBody,(response)=>{
            responseCallback(response.encryptedText);
        });
    }

    decryptAttribute(encryptedText: string, key: string,  responseCallback: ((response: any) => any)){
        let encryptAttributeRequestBody = {
            "key" : key,
            "encryptedText" : encryptedText
        };
        this.post("decryptAttribute",encryptAttributeRequestBody,responseCallback);
    }

    resolveViewRequest(factoryId: string, attributeVariableName: string, root: Data, responseCallback: ((response: any) => any)){
        let request={
            factoryId: factoryId,
            attributeVariableName: attributeVariableName,
            root: root.mapToJsonFromRoot(),
        };
        this.post("resolveViewRequest",request,responseCallback);
    }

    resolveViewList(factoryId: string, attributeVariableName: string, root: Data, responseCallback: ((response: any) => any)){
        let request={
            factoryId: factoryId,
            attributeVariableName: attributeVariableName,
            root: root.mapToJsonFromRoot(),
        };
        this.post("resolveViewList",request,responseCallback);
    }

    private post(url: string,  requestBody: object, responseCallback: ((response: any) => any)){
        let request: XMLHttpRequest = new XMLHttpRequest();
        request.open("POST",url);
        request.setRequestHeader("Content-type","application/json");

        request.onload=(e: ProgressEvent)=>{
            if (request.status >= 200 && request.status < 300) {
                responseCallback(JSON.parse(request.responseText));
                this.statusReporter.hideWaitAnimation();
            } else {
                this.statusReporter.reportError(request.responseText);
            }
        };
        this.statusReporter.showWaitAnimation();
        request.send(JSON.stringify(requestBody));
    }

    private get(url: string, responseCallback: ((response: any) => any)){
        let request: XMLHttpRequest = new XMLHttpRequest();
        request.open("GET",url);
        request.setRequestHeader("Content-type","application/json");

        request.onload=(e: ProgressEvent)=>{
            if (request.status >= 200 && request.status < 300) {
                responseCallback(JSON.parse(request.responseText));
                this.statusReporter.hideWaitAnimation();
            } else {
                this.statusReporter.reportError(request.responseText);
            }
        };
        this.statusReporter.showWaitAnimation();
        request.send();
    }

}