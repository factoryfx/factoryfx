//generated code don't edit manually
import { Widget } from "../../base/Widget";
import { Data } from "../../Data";
import { AttributeEditorModel } from "../attribute/AttributeEditorModel";
import { AttributeType } from "../../AttributeType";
import { StringAttributeEditor } from "../attribute/editors/StringAttributeEditor";
import { IntegerAttributeEditor } from "../attribute/editors/IntegerAttributeEditor";
import { LongAttributeEditor } from "../attribute/editors/LongAttributeEditor";
import { EnumAttributeEditor } from "../attribute/editors/EnumAttributeEditor";
import { EnumListAttributeEditor } from "../attribute/editors/EnumListAttributeEditor";
import { LocalDateAttributeEditor } from "../attribute/editors/LocalDateAttributeEditor";
import { BooleanAttributeEditor } from "../attribute/editors/BooleanAttributeEditor";
import { DoubleAttributeEditor } from "../attribute/editors/DoubleAttributeEditor";
import { ByteAttributeEditor } from "../attribute/editors/ByteAttributeEditor";
import { FileContentAttributeEditor } from "../attribute/editors/FileContentAttributeEditor";
import { StringListAttributeEditor } from "../attribute/editors/StringListAttributeEditor";
import { FloatAttributeEditor } from "../attribute/editors/FloatAttributeEditor";
import { FactoryAttributeEditor } from "../attribute/editors/FactoryAttributeEditor";
import { FactoryListAttributeEditor } from "../attribute/editors/FactoryListAttributeEditor";
import { FactoryViewAttributeEditor } from "../attribute/editors/FactoryViewAttributeEditor";
import { FactoryViewListAttributeEditor } from "../attribute/editors/FactoryViewListAttributeEditor";
import { FallbackEditor } from "../attribute/editors/FallbackEditor";
import { EncryptedStringAttributeEditorModel } from "../attribute/editors/EncryptedStringAttributeEditoModel";
export class FactoryEditor extends Widget {
    constructor(model, httpClient) {
        super();
        this.model = model;
        this.httpClient = httpClient;
        window.onpopstate = (event) => {
            this.back();
        };
    }
    createAttributeEditors() {
        this.attributeEditorModelsCreatedForData = this.model.factory.get();
        let attributeEditors = [];
        let counter = 0;
        for (let attributeAccessor of this.model.factory.get().listAttributeAccessor()) {
            let type = attributeAccessor.getAttributeMetadata().getType();
            let factoryEditorModel = null;
            if (type === AttributeType.StringAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new StringAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.IntegerAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new IntegerAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.LongAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new LongAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.EnumAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new EnumAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.EnumListAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new EnumListAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.LocalDateAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new LocalDateAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.BooleanAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new BooleanAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.DoubleAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new DoubleAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.ByteAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new ByteAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.FileContentAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new FileContentAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.StringListAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new StringListAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.FloatAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new FloatAttributeEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (type === AttributeType.EncryptedStringAttribute) {
                factoryEditorModel = new EncryptedStringAttributeEditorModel(this.model, this.httpClient);
            }
            if (type === AttributeType.FactoryAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new FactoryAttributeEditor(model.attributeAccessor.get(), model.inputId.get(), this.model, this.httpClient));
            }
            if (type === AttributeType.FactoryListAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new FactoryListAttributeEditor(model.attributeAccessor.get(), model.inputId.get(), this.model, this.httpClient));
            }
            if (type === AttributeType.FactoryPolymorphicAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new FactoryAttributeEditor(model.attributeAccessor.get(), model.inputId.get(), this.model, this.httpClient));
            }
            if (type === AttributeType.FactoryPolymorphicListAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new FactoryListAttributeEditor(model.attributeAccessor.get(), model.inputId.get(), this.model, this.httpClient));
            }
            if (type === AttributeType.FactoryViewAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new FactoryViewAttributeEditor(model.attributeAccessor.get(), model.inputId.get(), this.model, this.httpClient));
            }
            if (type === AttributeType.FactoryViewListAttribute) {
                factoryEditorModel = new AttributeEditorModel((model) => new FactoryViewListAttributeEditor(model.attributeAccessor.get(), model.inputId.get(), this.model, this.httpClient));
            }
            if (!factoryEditorModel) {
                factoryEditorModel = new AttributeEditorModel((model) => new FallbackEditor(model.attributeAccessor.get(), model.inputId.get()));
            }
            if (factoryEditorModel) {
                factoryEditorModel.inputId.set(counter.toString());
                factoryEditorModel.attributeAccessor.set(attributeAccessor);
                attributeEditors.push(factoryEditorModel);
            }
            counter++;
        }
        return attributeEditors;
    }
    render() {
        let container = document.createElement("div");
        if (!this.model.visible.get()) {
            return container;
        }
        let editDiv = document.createElement("div");
        //You can't construct DOM elements using normal constructors because you're supposed to go through document.createElement
        this.form = document.createElement("form");
        for (let attributeEditorWidget of this.getAttributeEditorModels()) {
            let formGroup = document.createElement("div");
            formGroup.className = "form-group row";
            formGroup.style.padding = "0rem 1rem";
            let div = document.createElement("div");
            div.className = "col-xl-10";
            let widget = attributeEditorWidget.getWidget();
            widget.append(div);
            formGroup.appendChild(widget.createLabel());
            formGroup.appendChild(div);
            this.form.appendChild(formGroup);
            this.form.appendChild(document.createElement("hr"));
        }
        editDiv.appendChild(this.form);
        container.className = "container-fluid";
        container.style.padding = "0px";
        container.appendChild(this.createBreadCrumb(this.model.factory.get()));
        let row = document.createElement("div");
        row.className = "row";
        let col4 = document.createElement("div");
        col4.className = "col-4";
        let col8 = document.createElement("div");
        col8.className = "col-8";
        container.appendChild(row);
        row.appendChild(col4);
        row.appendChild(col8);
        col8.appendChild(editDiv);
        col4.appendChild(this.createTree());
        return container;
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
                    this.model.edit(pathElement);
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
    back() {
        let path = this.model.factory.get().getPath();
        if (path.length >= 2) {
            this.model.edit(path[path.length - 2]);
        }
    }
    validate() {
        return this.form.reportValidity();
    }
    createTreeItem(variableName, data) {
        let ul = document.createElement("ul");
        ul.style.listStyleType = "circle";
        let li = document.createElement("li");
        let variableNameSpan = document.createElement("span");
        variableNameSpan.textContent = variableName;
        li.appendChild(variableNameSpan);
        li.appendChild(document.createElement("br"));
        if (this.model.factory.get() === data) {
            let span = document.createElement("span");
            span.className = "bg-primary text-white";
            span.style.whiteSpace = "nowrap";
            span.textContent = data.getDisplayText();
            li.appendChild(span);
        }
        else {
            let a = document.createElement("a");
            a.href = "#";
            a.textContent = data.getDisplayText();
            a.style.whiteSpace = "nowrap";
            a.onclick = (e) => {
                this.model.edit(data);
                e.preventDefault();
            };
            li.appendChild(a);
        }
        ul.appendChild(li);
        for (let attributeAccessor of data.listAttributeAccessor()) {
            let value = attributeAccessor.getValue();
            if (value instanceof Data) {
                li.appendChild(this.createTreeItem(attributeAccessor.getAttributeName(), value));
            }
            if (Array.isArray(value)) {
                for (let item of value) {
                    if (item instanceof Data) {
                        li.appendChild(this.createTreeItem(attributeAccessor.getAttributeName(), item));
                    }
                }
            }
        }
        return ul;
    }
    createTree() {
        let treeCard = document.createElement("div");
        treeCard.className = "card";
        treeCard.style.overflowX = "scroll";
        treeCard.style.marginLeft = "15px";
        treeCard.style.height = "600px";
        let cardBody = document.createElement("div");
        cardBody.className = "card-body";
        cardBody.appendChild(this.createTreeItem("root", this.model.factory.get().getRoot()));
        treeCard.appendChild(cardBody);
        return treeCard;
    }
    getAttributeEditorModels() {
        if (!this.attributeEditorModels) {
            this.attributeEditorModels = this.createAttributeEditors();
        }
        return this.attributeEditorModels;
    }
    bindModel() {
        let newData = this.model.getFactory();
        window.history.pushState(null, "", window.location.pathname + "#" + newData.getPath().map(factory => factory.getDisplayText()).join('/'));
        if (newData != this.attributeEditorModelsCreatedForData) {
            this.attributeEditorModels = this.createAttributeEditors();
        }
        return super.bindModel();
    }
}
