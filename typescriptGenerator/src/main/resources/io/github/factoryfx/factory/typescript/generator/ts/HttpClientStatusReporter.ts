import {Data} from "./Data";
import {WaitAnimationModel} from "./widget/waitannimation/WaitAnimationModel";
import {RootModel} from "./widget/root/RootModel";


export class HttpClientStatusReporter {
    private rootModel?: RootModel;
    setRootModel(rootModel: RootModel){
        this.rootModel=rootModel;
    }

    private timer?: number;
    showWaitAnimation(){
        if (!this.rootModel){
            return;
        }
        this.timer = setTimeout(()=> { //delay wait animation to avoid flicker
            this.rootModel!.showWaitAnimation();
        }, 1000);
    }

    hideWaitAnimation(){
        if (this.timer){
            clearTimeout(this.timer);
        }
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