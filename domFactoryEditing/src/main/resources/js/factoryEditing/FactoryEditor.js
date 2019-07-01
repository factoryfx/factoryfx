import { DomUtility } from "./DomUtility";
export class FactoryEditor {
    constructor(attributeEditorCreator, waitAnimation) {
        this.attributeEditorCreator = attributeEditorCreator;
        this.waitAnimation = waitAnimation;
        this.container = document.createElement("div");
    }
    createBreadCrumb(data) {
        let nav = document.createElement("nav");
        nav.setAttribute("aria-label", "breadcrumb");
        let ol = document.createElement("ol");
        ol.className = "breadcrumb";
        ol.style.borderRadius = "0";
        let counter = 0;
        let path = data.getPath();
        for (let pathElement of path) {
            let li = document.createElement("li");
            if (counter == path.length - 1) {
                li.className = "breadcrumb-item active";
                li.textContent = pathElement.getDisplayText();
            }
            else {
                let a = document.createElement("a");
                a.href = "#";
                a.textContent = pathElement.getDisplayText();
                a.onclick = (e) => {
                    e.preventDefault();
                    this.edit(pathElement);
                    return false;
                };
                li.className = "breadcrumb-item";
                li.appendChild(a);
            }
            ol.appendChild(li);
            counter++;
        }
        nav.appendChild(ol);
        return nav;
    }
    setOnFactoryChange(event) {
        this.factoryChangeEvent = event;
    }
    edit(data) {
        this.currentData = data;
        DomUtility.clear(this.container);
        let editDiv = document.createElement("div");
        //You can't construct DOM elements using normal constructors because you're supposed to go through document.createElement
        this.form = document.createElement("form");
        let counter = 0;
        for (let listAttributeAccessorElement of data.listAttributeAccessor()) {
            let formGroup = document.createElement("div");
            formGroup.className = "form-group row";
            formGroup.style.padding = "0rem 1rem";
            let label = document.createElement("label");
            label.htmlFor = counter.toString();
            label.className = "col-xl-2 col-form-label";
            label.style.textOverflow = "clip";
            label.style.overflow = "hidden";
            label.textContent = listAttributeAccessorElement.getLabelText("en");
            let div = document.createElement("div");
            div.className = "col-xl-10";
            div.appendChild(this.attributeEditorCreator.create(listAttributeAccessorElement, counter.toString(), this, this.waitAnimation).create());
            formGroup.appendChild(label);
            formGroup.appendChild(div);
            this.form.appendChild(formGroup);
            this.form.appendChild(document.createElement("hr"));
            counter++;
        }
        editDiv.appendChild(this.form);
        if (this.factoryChangeEvent) {
            this.factoryChangeEvent(data);
        }
        this.form = document.createElement("form");
        this.container.className = "container-fluid";
        this.container.style.padding = "0px";
        this.container.appendChild(this.createBreadCrumb(data));
        let row = document.createElement("div");
        row.className = "row";
        let col4 = document.createElement("div");
        col4.className = "col-4";
        let col8 = document.createElement("div");
        col8.className = "col-8";
        this.container.appendChild(row);
        row.appendChild(col4);
        row.appendChild(col8);
        col8.appendChild(editDiv);
        let treeRoot = this.createTree(data.getRoot());
        treeRoot.style.overflowX = "scroll";
        col4.appendChild(treeRoot);
    }
    back() {
        let path = this.currentData.getPath();
        if (path.length >= 2) {
            this.edit(path[path.length - 2]);
        }
    }
    validate() {
        return this.form.reportValidity();
    }
    getCurrentData() {
        return this.currentData;
    }
    create() {
        return this.container;
    }
    createTree(root) {
        let card = document.createElement("div");
        card.className = "card";
        let cardBody = document.createElement("div");
        cardBody.className = "card-body";
        cardBody.appendChild(this.createTreeItem(root));
        card.appendChild(cardBody);
        card.style.marginLeft = "15px";
        return card;
    }
    createTreeItem(data) {
        let ul = document.createElement("ul");
        for (let child of data.getChildrenFlat()) {
            let li = document.createElement("li");
            let a = document.createElement("a");
            a.href = "#";
            a.textContent = child.getDisplayText();
            a.style.whiteSpace = "nowrap";
            a.onclick = (e) => {
                this.edit(child);
                e.preventDefault();
            };
            li.appendChild(a);
            li.appendChild(this.createTreeItem(child));
            ul.appendChild(li);
        }
        return ul;
    }
}
