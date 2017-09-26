'use strict';

Vue.component('factory-editor', {

    props: [],
    template: `
        <div>
            <button clas="btn btn-primary" v-on:click="getFactoryTree">Reverse Message</button>
            {{factory}}
        </div>
    `,
    data: function() {
        return {
            factory: "dgdgfdgfdgfd"
        };
    },
    methods: {
        getFactoryTree: function() {
            var rq = new XMLHttpRequest();

            rq.onreadystatechange = function(vm) {
                if (this.readyState === XMLHttpRequest.DONE) {
                    if (this.status === 200) {
                        vm.factory = this.responseText;
                    } else {
                        vm.factory = "Request Failed";
                    }
                }
            }.bind(rq, this);

            rq.open("POST", "applicationServer/prepareNewFactory");
            rq.send();



            // var request = new XMLHttpRequest();
            // request.open("POST","applicationServer/prepareNewFactory");
            // request.setRequestHeader("X-Test","test1");
            // request.setRequestHeader("X-Test","test2");
            // request.addEventListener('load', function(event) {
            //     if (request.status >= 200 && request.status < 300) {
            //         vm.factory = JSON.parse(request.body);
            //     } else {
            //         console.warn(request.statusText, request.responseText);
            //     }
            // });
            // request.send();
            // var xhr = new XMLHttpRequest();
            // xhr({
            //     uri: "http://localhost:8087/applicationServer",
            //     headers: {
            //         "Content-Type": "application/json"
            //     }
            // }, (err, resp, body) => {
            //     this.list = JSON.parse(resp.body);
            // });
        }
    }
});

