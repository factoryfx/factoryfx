import {AttributeEditorWidget} from "../AttributeEditorWidget";
import {FactoryEditorModel} from "../../factoryeditor/FactoryEditorModel";
import {HttpClient} from "../../../HttpClient";
import {EncryptedStringAttributeEditorModel} from "./EncryptedStringAttributeEditoModel";
import {BootstrapUtility} from "../../../BootstrapUtility";


export class EncryptedStringAttributeEditor extends AttributeEditorWidget{
    private encytptedTextInput: HTMLInputElement= document.createElement("input");
    private decryptButton: HTMLButtonElement= document.createElement("button");
    private inputKey: HTMLInputElement= document.createElement("input");
    private inputValue: HTMLInputElement= document.createElement("input");
    private encryptButton: HTMLButtonElement= document.createElement("button");

    constructor(private model: EncryptedStringAttributeEditorModel, private factoryEditorModel: FactoryEditorModel, private httpClient: HttpClient) {
        super(model.attributeAccessor.get()!,model.inputId.get()!);
    }

    protected render(): HTMLElement{
        let form: HTMLFormElement= document.createElement("form");
        form.className="form-inline";

        this.inputKey.id=this.inputId.toString();
        this.inputKey.className="form-control";
        this.inputKey.type="text";
        this.inputKey.style.marginRight="3px";
        this.inputKey.placeholder="Key";
        form.appendChild(this.inputKey);

        let encytptedTextLabel: HTMLLabelElement= document.createElement("label");
        encytptedTextLabel.textContent="Encrypted";
        encytptedTextLabel.style.marginRight="3px";
        form.appendChild(encytptedTextLabel);


        this.encytptedTextInput.id = this.inputId.toString();
        this.encytptedTextInput.type = "text";
        this.encytptedTextInput.readOnly = true;
        this.decryptButton.textContent = "decrypt";
        let inputGroup1 = BootstrapUtility.createInputGroup(this.encytptedTextInput, this.decryptButton);
        inputGroup1.style.marginRight = "3px";
        form.appendChild(inputGroup1);


        let labelValue: HTMLLabelElement= document.createElement("label");
        labelValue.textContent="Value";
        labelValue.style.marginRight="3px";
        form.appendChild(labelValue);


        this.inputValue.id = this.inputId.toString();
        this.inputValue.type = "text";
        this.encryptButton.textContent="encrypt";
        let inputGroup2 = BootstrapUtility.createInputGroup(this.inputValue, this.encryptButton);
        inputGroup2.style.marginRight = "3px";
        form.appendChild(inputGroup2);

        //input.required=!this.attributeAccessor.getAttributeMetadata().nullable();


        return form;
    }

    public bindModel(): any {
        this.renderOnce();

        this.encytptedTextInput.value=this.attributeAccessor.getValue().encryptedString;

        this.decryptButton.disabled=!(!!this.model.key.get());
        this.decryptButton.onclick=(e)=>{
            this.httpClient.decryptAttribute(
                this.attributeAccessor.getValue().encryptedString,
                this.inputKey.value,
                (response)=>{
                    this.inputValue.value=response.text;
                }
            );
        };

        if (this.model.key.get()){
            this.inputKey.value=this.model.key.get()!;
        }
        this.inputKey.oninput=(e) => {
            this.model.key.set(this.inputKey.value);
        };

        this.encryptButton.disabled=!(!!this.model.key.get());
        this.encryptButton.onclick=(e)=>{
            this.httpClient.encryptAttribute(
                this.inputValue.value,
                this.inputKey.value,
                (encryptedText: string)=>{
                    this.attributeAccessor.setValue({
                        encryptedString: encryptedText
                    });
                }
            );

        };

        this.inputValue.disabled=!(!!this.model.key.get());
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