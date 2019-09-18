//generated code don't edit manually
import { WidgetModel } from "../../base/WidgetModel";
import { BooleanValue } from "../../base/BooleanValue";
import { HistoryWidget } from "./HistoryWidget";
export class HistoryWidgetModel extends WidgetModel {
    constructor(httpClient) {
        super();
        this.httpClient = httpClient;
        this.visible = new BooleanValue();
    }
    createWidget() {
        return new HistoryWidget(this);
    }
    setViewModel(viewModel) {
        this.viewModel = viewModel;
    }
}
