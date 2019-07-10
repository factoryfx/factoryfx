import {AttributeAccessor} from "./AttributeAccessor";
import {AttributeEditor} from "./AttributeEditor";
import {FactoryEditor} from "./FactoryEditor";
import {WaitAnimation} from "./WaitAnimation";
import {HttpUtility} from "./HttpUtility";

export class AttributeEditorEncryptedStringAttribute implements AttributeEditor{

    constructor(private attributeAccessor: AttributeAccessor<any>, private inputId: string, private factoryEditor: FactoryEditor, private waitAnimation: WaitAnimation) {

    }

    create(): HTMLElement{
        let form: HTMLFormElement= document.createElement("form");
        form.className="form-inline";

        let labelValue: HTMLLabelElement= document.createElement("label");
        labelValue.textContent="Value";
        form.appendChild(labelValue);

        let inputValue: HTMLInputElement= document.createElement("input");
        inputValue.id=this.inputId.toString();
        inputValue.className="form-control";
        inputValue.type="text";
        form.appendChild(inputValue);


        let labelKey: HTMLLabelElement= document.createElement("label");
        labelKey.textContent="Key";
        form.appendChild(labelKey);

        let inputKey: HTMLInputElement= document.createElement("input");
        inputKey.id=this.inputId.toString();
        inputKey.className="form-control";
        inputKey.type="text";
        form.appendChild(inputKey);

        let encryptButton: HTMLButtonElement= document.createElement("button");
        encryptButton.type="button";
        encryptButton.textContent="encrypt";
        encryptButton.onclick=(e)=>{
            let encryptAttributeRequestBody = {
                "text" : inputValue.value,
                "key" : inputKey.value,
                "factoryId" : this.factoryEditor.getCurrentData().getId(),
                "attributeVariableName" : this.attributeAccessor.getAttributeName(),
                "root" : this.factoryEditor.getCurrentData().getRoot().mapToJsonFromRoot()
            };
            HttpUtility.post("encryptAttribute",encryptAttributeRequestBody,this.waitAnimation,(response)=>{

            });
        };
        encryptButton.className="btn btn-secondary";
        form.appendChild(encryptButton);


        let decrypttButton: HTMLButtonElement= document.createElement("button");
        decrypttButton.type="button";
        decrypttButton.textContent="decrypt";
        decrypttButton.onclick=(e)=>{
            let encryptAttributeRequestBody = {
                "key" : inputKey.value,
                "factoryId" : this.factoryEditor.getCurrentData().getId(),
                "attributeVariableName" : this.attributeAccessor.getAttributeName(),
                "root" : this.factoryEditor.getCurrentData().getRoot().mapToJsonFromRoot()
            };
            HttpUtility.post("decryptAttribute",encryptAttributeRequestBody,this.waitAnimation,(response)=>{
                inputValue.value=response.text;
            });
        };
        decrypttButton.className="btn btn-secondary";
        form.appendChild(decrypttButton);

        //input.required=!this.attributeAccessor.getAttributeMetadata().nullable();


        return form;
    }



    // decryptMessage(privateKeyBase64: string, ciphertext: string): void {
        // const importedKey = await window.crypto.subtle.importKey(
        //     'raw',
        //     "jNNxjStGsrwgu+4G5DYc9Q==",
        //     "AES-GCM",
        //     true, [
        //         "encrypt",
        //         "decrypt"
        //     ]
        // );

        // window.crypto.subtle.generateKey()

        // b64 = removeLines(b64);
        // let arrayBuffer = this.base64ToArrayBuffer(privateKeyBase64);

        // window.crypto.subtle.importKey(
        //     "raw",
        //     arrayBuffer,
        //     {
        //         name: "AES-CBC",
        //         hash: {name: "SHA-256"}
        //     },
        //     true,
        //     ["decrypt"]
        // ).then((importedPrivateKey) => {
        //     console.log(importedPrivateKey);
        //
        //     window.crypto.subtle.decrypt(
        //         {
        //             name: "AES-CBC"
        //         },
        //         importedPrivateKey,
        //         this.base64ToArrayBuffer(ciphertext)
        //     ).then( (result)=>{
        //         console.log(result);
        //     });
        //
        // });


    // }

    //
    // base64ToArrayBuffer(b64: string): Uint8Array {
    //     var byteString = window.atob(b64);
    //     var byteArray: Uint8Array = new Uint8Array(byteString.length);
    //     for(var i=0; i < byteString.length; i++) {
    //         byteArray[i] = byteString.charCodeAt(i);
    //     }
    //
    //     return byteArray;
    // }



}