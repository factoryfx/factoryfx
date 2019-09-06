//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { SaveWidget } from "./SaveWidget";
import { BooleanValue } from "../../base/BooleanValue";
export class SaveWidgetModel extends WidgetModel {
    constructor(baseVersionId, rootFactory, httpClient) {
        super();
        this.baseVersionId = baseVersionId;
        this.rootFactory = rootFactory;
        this.httpClient = httpClient;
        this.visible = new BooleanValue();
    }
    createWidget() {
        return new SaveWidget(this);
    }
    setViewModel(viewModel) {
        this.viewModel = viewModel;
    }
}
