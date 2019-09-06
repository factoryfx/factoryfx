import {Data} from "./Data";
import {WaitAnimationModel} from "./widget/waitannimation/WaitAnimationModel";
import {RootModel} from "./widget/root/RootModel";


export class HttpClientStatusReporter {
    private rootModel?: RootModel;
    setRootModel(rootModel: RootModel){
        this.rootModel=rootModel;
    }

    showWaitAnimation(){
        if (!this.rootModel){
            return;
        }
        this.rootModel.showWaitAnimation();
    }

    hideWaitAnimation(){
        if (!this.rootModel){
            return;
        }
        this.rootModel.hideWaitAnimation()
    }

    reportError(error: string){
        if (!this.rootModel){
            console.log(error);
            return;
        }
        this.rootModel.reportError(error);
    }


}