import {WaitAnimation} from "./WaitAnimation";

export class HttpUtility {

    public static post(url: string,  requestBody: object, waitAnimation: WaitAnimation, responseCallback: ((response: object) => any),){
        let request: XMLHttpRequest = new XMLHttpRequest();
        request.open("POST",url);
        request.setRequestHeader("Content-type","application/json");

        request.onload=(e)=>{
            if (request.status >= 200 && request.status < 300) {
                responseCallback(JSON.parse(request.responseText));
                waitAnimation.hide();
            } else {
                waitAnimation.reportError(request.responseText);
            }
        };
        waitAnimation.show();
        request.send(JSON.stringify(requestBody));
    }

    public static get(url: string, waitAnimation: WaitAnimation, responseCallback: ((response: object) => any)){
        let request: XMLHttpRequest = new XMLHttpRequest();
        request.open("GET",url);
        request.setRequestHeader("Content-type","application/json");

        request.onload=(e)=>{
            if (request.status >= 200 && request.status < 300) {
                responseCallback(JSON.parse(request.responseText));
                waitAnimation.hide();
            } else {
                waitAnimation.reportError(request.responseText);
            }
        };
        waitAnimation.show();
        request.send();
    }

}