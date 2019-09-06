import {WidgetModel} from "../../base/WidgetModel";
import {View} from "./View";
import {NavbarModel} from "../navbar/NavbarModel";
import {FactoryEditorModel} from "../factoryeditor/FactoryEditorModel";
import {SaveWidgetModel} from "../save/SaveWidgetModel";
import {FactoryUpdateResultModel} from "../factoryUpdateResult/FactoryUpdateResultModel";
import {BooleanValue} from "../../base/BooleanValue";


export class ViewModel extends WidgetModel<View> {
    public readonly visible: BooleanValue = new BooleanValue();

    constructor(public factoryEditor: FactoryEditorModel, public saveWidget: SaveWidgetModel,  public factoryUpdateResult: FactoryUpdateResultModel,  public navbar: NavbarModel) {
        super();
    }


    showFactoryEditor(){
        this.factoryEditor.visible.set(true);
        this.saveWidget.visible.set(false);
        this.factoryUpdateResult.visible.set(false);
    }

    showSaveContent(){
        this.factoryEditor.visible.set(false);
        this.saveWidget.visible.set(true);
        this.factoryUpdateResult.visible.set(false);
    }

    showFactoryUpdateResult(){
        this.factoryEditor.visible.set(false);
        this.saveWidget.visible.set(false);
        this.factoryUpdateResult.visible.set(true);
    }

    protected createWidget(): View {
        return new View(this);
    }
}