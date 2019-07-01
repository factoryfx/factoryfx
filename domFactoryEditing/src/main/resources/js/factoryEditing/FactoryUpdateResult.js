export class FactoryUpdateResult {
    constructor(response) {
        this.response = response;
    }
    create() {
        let resultDisplay = document.createElement("div");
        resultDisplay.className = "alert alert-success";
        resultDisplay.setAttribute("role", "alert");
        let pre = document.createElement("pre");
        let code = document.createElement("code");
        pre.appendChild(code);
        code.textContent = this.response.log;
        resultDisplay.appendChild(pre);
        let button = document.createElement("button");
        button.className = "btn btn-outline-secondary";
        button.textContent = "continue";
        button.onclick = (e) => {
            window.location.reload();
        };
        resultDisplay.appendChild(button);
        return resultDisplay;
    }
}
