import {WidgetModel} from "../../base/WidgetModel";
import {StringValue} from "../../base/StringValue";
import {FactoryValue} from "../../base/FactoryValue";
import {BooleanValue} from "../../base/BooleanValue";
import {Data} from "../../Data";
import {HttpClient} from "../../HttpClient";
import {ViewModel} from "../view/ViewModel";
import {HistoryWidget} from "./HistoryWidget";

export class HistoryWidgetModel extends WidgetModel<HistoryWidget> {
    public readonly visible: BooleanValue = new BooleanValue();

    constructor(public httpClient: HttpClient){
        super()
    }

    protected createWidget(): HistoryWidget {
        return new HistoryWidget(this);
    }

    viewModel?: ViewModel;
    setViewModel(viewModel: ViewModel) {
        this.viewModel=viewModel;
    }

}