import {Widget} from "../../base/Widget";
import {HistoryWidgetModel} from "./HistoryWidgetModel";
import {StoredDataMetadata} from "../../StoredDataMetadata";

export class HistoryWidget extends Widget {

    constructor(private model: HistoryWidgetModel){
        super();
    }

    render(): HTMLElement {
        let div: HTMLDivElement = document.createElement("div");
        if (!this.model.visible.get()) {
            return div;
        }

        let h: HTMLHeadingElement = document.createElement("h4");
        div.appendChild(h);
        h.textContent = "History";

        let table: HTMLTableElement = document.createElement("table");
        div.appendChild(table);
        table.className = "table";

        let thead: HTMLElement = document.createElement("thead");
        table.appendChild(thead);
        let trHeader: HTMLElement = document.createElement("tr");
        thead.appendChild(trHeader);

        {
            let th: HTMLElement = document.createElement("th");
            trHeader.appendChild(th);
            th.textContent = "time";
            th.setAttribute("scope","col");
        }
        {
            let th: HTMLElement = document.createElement("th");
            trHeader.appendChild(th);
            th.textContent = "user";
            th.setAttribute("scope","col");
        }
        {
            let th: HTMLElement = document.createElement("th");
            trHeader.appendChild(th);
            th.textContent = "comment";
            th.setAttribute("scope","col");
        }

        let tbody: HTMLElement = document.createElement("tbody");
        table.appendChild(tbody);


        this.model.httpClient.getHistoryFactoryList(
            (storedDataMetadataList: StoredDataMetadata[]) => {
                for (let data of storedDataMetadataList) {
                    let tr: HTMLTableRowElement = document.createElement("tr");
                    tbody.appendChild(tr);

                    {
                        let td: HTMLTableDataCellElement = document.createElement("td");
                        tr.appendChild(td);
                        td.textContent = "" + data.creationTime;
                        td.setAttribute("scope","row");
                    }

                    {
                        let td: HTMLTableDataCellElement = document.createElement("td");
                        tr.appendChild(td);
                        td.textContent = "" + data.user;
                        td.setAttribute("scope","row");
                    }

                    {
                        let td: HTMLTableDataCellElement = document.createElement("td");
                        tr.appendChild(td);
                        td.textContent = "" + data.comment;
                        td.setAttribute("scope","row");
                    }

                }
              }
        );

        return div;
    }


}