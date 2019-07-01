export class HttpUtility {
    static post(url, requestBody, waitAnimation, responseCallback) {
        let request = new XMLHttpRequest();
        request.open("POST", url);
        request.setRequestHeader("Content-type", "application/json");
        request.onload = (e) => {
            if (request.status >= 200 && request.status < 300) {
                responseCallback(JSON.parse(request.responseText));
                waitAnimation.hide();
            }
            else {
                waitAnimation.reportError(request.responseText);
            }
        };
        waitAnimation.show();
        request.send(JSON.stringify(requestBody));
    }
    static get(url, waitAnimation, responseCallback) {
        let request = new XMLHttpRequest();
        request.open("GET", url);
        request.setRequestHeader("Content-type", "application/json");
        request.onload = (e) => {
            if (request.status >= 200 && request.status < 300) {
                responseCallback(JSON.parse(request.responseText));
                waitAnimation.hide();
            }
            else {
                waitAnimation.reportError(request.responseText);
            }
        };
        waitAnimation.show();
        request.send();
    }
}
