//generated code don't edit manually
import { Widget } from "../../base/Widget";
export class HistoryWidget extends Widget {
    constructor(model) {
        super();
        this.model = model;
    }
    render() {
        let div = document.createElement("div");
        if (!this.model.visible.get()) {
            return div;
        }
        let h = document.createElement("h4");
        div.appendChild(h);
        h.textContent = "History";
        let table = document.createElement("table");
        div.appendChild(table);
        table.className = "table";
        let thead = document.createElement("thead");
        table.appendChild(thead);
        let trHeader = document.createElement("tr");
        thead.appendChild(trHeader);
        {
            let th = document.createElement("th");
            trHeader.appendChild(th);
            th.textContent = "time";
            th.setAttribute("scope", "col");
        }
        {
            let th = document.createElement("th");
            trHeader.appendChild(th);
            th.textContent = "user";
            th.setAttribute("scope", "col");
        }
        {
            let th = document.createElement("th");
            trHeader.appendChild(th);
            th.textContent = "comment";
            th.setAttribute("scope", "col");
        }
        let tbody = document.createElement("tbody");
        table.appendChild(tbody);
        this.model.httpClient.getHistoryFactoryList((storedDataMetadataList) => {
            for (let data of storedDataMetadataList) {
                let tr = document.createElement("tr");
                tbody.appendChild(tr);
                {
                    let td = document.createElement("td");
                    tr.appendChild(td);
                    td.textContent = "" + data.creationTime;
                    td.setAttribute("scope", "row");
                }
                {
                    let td = document.createElement("td");
                    tr.appendChild(td);
                    td.textContent = "" + data.user;
                    td.setAttribute("scope", "row");
                }
                {
                    let td = document.createElement("td");
                    tr.appendChild(td);
                    td.textContent = "" + data.comment;
                    td.setAttribute("scope", "row");
                }
            }
        });
        return div;
    }
}
