package de.factoryfx.process;


public class SubscriptionProcess implements Process<String> {

    private final SoapCaller soapCaller;
    private final int value;

    public SubscriptionProcess(SoapCaller soapCaller, int value) {
        this.soapCaller = soapCaller;
        this.value = value;
    }

    @Override
    public String run() {
        soapCaller.payment(value);
        return "";
    }
}
