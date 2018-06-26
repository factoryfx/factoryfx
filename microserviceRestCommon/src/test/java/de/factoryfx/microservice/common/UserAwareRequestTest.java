package de.factoryfx.microservice.common;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Test;

public class UserAwareRequestTest {

    public static class TestRequest{
        public String name;
    }

    public static class Test123<T>{
        public UserAwareRequest<T> nestedRequest;
    }



    @Test
    public void test_json(){
        TestRequest testRequest = new TestRequest();
        testRequest.name="123";
        UserAwareRequest<TestRequest> userAwareRequest = new UserAwareRequest<>("","", testRequest);

        ObjectMapperBuilder.build().copy(userAwareRequest);
    }

    @Test
    public void test_json_generic(){
        Test123<TestRequest> testRequest = new Test123<>();
        testRequest.nestedRequest=new UserAwareRequest<>("","",new TestRequest());
        testRequest.nestedRequest.request.name="123";

        ObjectMapperBuilder.build().copy(testRequest);
    }



}