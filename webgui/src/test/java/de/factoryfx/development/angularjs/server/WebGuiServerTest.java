package de.factoryfx.development.angularjs.server;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.BigDecimalAttribute;
import org.junit.Assert;
import org.junit.Test;

public class WebGuiServerTest {

    public static class TestBigDecimalLiveObject implements LiveObject{

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void accept(Object visitor) {

        }
    }

    public static class TestBigDecimal extends FactoryBase<TestBigDecimalLiveObject,TestBigDecimal>{
        public final BigDecimalAttribute value = new BigDecimalAttribute(new AttributeMetadata());

        @Override
        protected TestBigDecimalLiveObject createImp(Optional<TestBigDecimalLiveObject> previousLiveObject) {
            return null;
        }
    }


    @Test
    public void test_bigdecimal() throws IOException {
        TestBigDecimal testBigDecimal = new TestBigDecimal();
        testBigDecimal.value.set(new BigDecimal("12445.67"));

        WebGuiServer webGuiServer = new WebGuiServer(null,null,null);
        ObjectMapper objectMapper = webGuiServer.getObjectMapper();

        String data = objectMapper.writeValueAsString(testBigDecimal);
        System.out.println(data);

        TestBigDecimal readed = objectMapper.readValue(data,TestBigDecimal.class);
        readed.reconstructMetadataDeepRoot();
        Assert.assertEquals("12445.67",readed.value.get().toString());



    }
}