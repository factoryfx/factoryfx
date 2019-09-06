//generated code don't edit manually
import { RootModel } from "../widget/root/RootModel";
import { WaitAnimationModel } from "../widget/waitannimation/WaitAnimationModel";
import { DynamicDataDictionary } from "../DynamicDataDictionary";
import { NavbarModel } from "../widget/navbar/NavbarModel";
import { DynamicData } from "../DynamicData";
import { NavitemModel } from "../widget/navitem/NavitemModel";
import { FactoryEditorModel } from "../widget/factoryeditor/FactoryEditorModel";
import { HttpClient } from "../HttpClient";
import { ViewModel } from "../widget/view/ViewModel";
import { SaveWidgetModel } from "../widget/save/SaveWidgetModel";
import { FactoryUpdateResultModel } from "../widget/factoryUpdateResult/FactoryUpdateResultModel";
import { DomUtility } from "../DomUtility";
import { ErrorReporterModel } from "../widget/errorreporter/ErrorReporterModel";
import { HttpClientStatusReporter } from "../HttpClientStatusReporter";
import { BootstrapUtility } from "../BootstrapUtility";
export class Main {
    main(target) {
        target.append(BootstrapUtility.createProgressBar());
        let httpClientStatusReporter = new HttpClientStatusReporter();
        let httpClient = new HttpClient(httpClientStatusReporter);
        httpClient.getMetadata((dynamicDataDictionaryJson, guiConfiguration) => {
            let dynamicDataDictionary = new DynamicDataDictionary();
            dynamicDataDictionary.mapFromJson(dynamicDataDictionaryJson);
            httpClient.prepareNewFactory((rootJson, baseVersionId) => {
                let root = new DynamicData();
                root.mapFromJsonFromRootDynamic(rootJson, dynamicDataDictionary);
                let factoryEditorModel = new FactoryEditorModel(httpClient);
                let navItems = [];
                for (let navItemsJson of guiConfiguration.navBarItems) {
                    let navitemModel = new NavitemModel(root.getChildFromRoot(navItemsJson.factoryId), factoryEditorModel);
                    navItems.push(navitemModel);
                }
                let saveWidgetModel = new SaveWidgetModel(baseVersionId, root, httpClient);
                let navbarModel = new NavbarModel(navItems, factoryEditorModel);
                let factoryUpdateResultModel = new FactoryUpdateResultModel();
                let viewModel = new ViewModel(factoryEditorModel, saveWidgetModel, factoryUpdateResultModel, navbarModel);
                navbarModel.setViewModel(viewModel);
                saveWidgetModel.setViewModel(viewModel);
                let rootModel = new RootModel(viewModel, new WaitAnimationModel(), new ErrorReporterModel());
                viewModel.showFactoryEditor();
                factoryEditorModel.edit(root);
                httpClientStatusReporter.setRootModel(rootModel);
                DomUtility.clear(target);
                target.append(rootModel.getWidget().render());
            });
        });
    }
}
