import { BootstrapUtility } from "../BootstrapUtility";
export class FactorySelectDialog {
    constructor(parent, possibleValues, selectCallback) {
        this.parent = parent;
        this.possibleValues = possibleValues;
        this.selectCallback = selectCallback;
    }
    show() {
        let dialog = document.createElement("dialog");
        this.parent.appendChild(dialog);
        let list = document.createElement("ul");
        list.className = "list-group";
        dialog.appendChild(list);
        for (let possibleValue of this.possibleValues) {
            let li = document.createElement("li");
            list.appendChild(li);
            li.className = "list-group-item";
            let selectButton = BootstrapUtility.createButtonSuccess();
            li.appendChild(selectButton);
            selectButton.style.marginLeft = "6px";
            selectButton.onclick = (e) => {
                this.selectCallback(possibleValue);
                dialog.close();
            };
            selectButton.textContent = possibleValue.getDisplayText();
        }
        let closeButton = BootstrapUtility.createButtonSecondary();
        dialog.appendChild(closeButton);
        closeButton.style.marginTop = "6px";
        closeButton.onclick = (e) => {
            dialog.close();
        };
        closeButton.textContent = "Cancel";
        dialog.showModal();
    }
}
