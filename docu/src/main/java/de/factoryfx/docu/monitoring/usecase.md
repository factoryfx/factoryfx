#Monitoring
Most application need some kind of runtime monitoring for example a dashboard with requests per second.<br>
To get monitoring data from the running application you can use the factory visitor.
That visitor can be executed from applicationServer and will visit all liveobjects. 
The liveobjects can add monitoring data to the visitor.

This example use the metric library to monitor a http server.

[**code**](https://github.com/factoryfx/factoryfx/tree/master/docu/src/main/java/de/factoryfx/docu/monitoring)