//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { Navbar } from "./Navbar";
import { StringValue } from "../../base/StringValue";
export class NavbarModel extends WidgetModel {
    constructor(navItems, factoryEditorModel) {
        super();
        this.navItems = navItems;
        this.factoryEditorModel = factoryEditorModel;
        this.projectName = new StringValue();
    }
    createWidget() {
        return new Navbar(this);
    }
    setViewModel(viewModel) {
        this.viewModel = viewModel;
    }
}
