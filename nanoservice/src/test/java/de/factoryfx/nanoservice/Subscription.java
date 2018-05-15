package de.factoryfx.nanoservice;

public class Subscription implements NanoserviceRoot<PaymentRequest> {
    private final String example;
    public Subscription(String example) {
        this.example=example;
    }

    @Override
    public PaymentRequest run() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.example=example;
        return paymentRequest;
    }
}
