import {Widget} from "../../base/Widget";
import {FactoryUpdateResultModel} from "./FactoryUpdateResultModel";


export class FactoryUpdateResult extends Widget{
    constructor(private model: FactoryUpdateResultModel){
        super();
    }

    render(): HTMLElement {
        let resultDisplay: HTMLDivElement = document.createElement("div");
        if (!this.model.visible.get()){
            return resultDisplay;
        }
        resultDisplay.className="alert alert-success";
        resultDisplay.setAttribute("role","alert");
        let pre = document.createElement("pre");
        let code = document.createElement("code");
        pre.appendChild(code);
        code.textContent=this.model.updatelog.get()!;
        resultDisplay.appendChild(pre);

        let button: HTMLButtonElement = document.createElement("button");
        button.className="btn btn-outline-secondary";
        button.textContent="continue";
        button.onclick=(e)=>{
            window.location.reload();
        };
        resultDisplay.appendChild(button);
        return resultDisplay;
    }

}