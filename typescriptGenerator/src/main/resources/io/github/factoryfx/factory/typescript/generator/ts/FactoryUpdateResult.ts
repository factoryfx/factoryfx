import {NavItem} from "./NavItem";
import {HttpUtility} from "./HttpUtility";
import {Widget} from "./Widget";
import {FactoryEditor} from "./FactoryEditor";
import {Data} from "./Data";

export class FactoryUpdateResult implements Widget{
    constructor(private response: any){

    }

    create(): HTMLElement{
        let resultDisplay: HTMLDivElement = document.createElement("div");
        resultDisplay.className="alert alert-success";
        resultDisplay.setAttribute("role","alert");
        let pre = document.createElement("pre");
        let code = document.createElement("code");
        pre.appendChild(code);
        code.textContent=this.response.log;
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