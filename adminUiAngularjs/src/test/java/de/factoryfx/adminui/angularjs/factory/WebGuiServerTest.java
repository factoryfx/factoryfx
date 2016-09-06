package de.factoryfx.adminui.angularjs.factory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.LiveObject;
import de.factoryfx.factory.attribute.AttributeMetadata;
import de.factoryfx.factory.attribute.util.BigDecimalAttribute;
import de.factoryfx.factory.attribute.util.LongAttribute;
import org.junit.Assert;
import org.junit.Test;

public class WebGuiServerTest {

    public static class TestBigDecimalLiveObject implements LiveObject {

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

    public static class TestBigDecimal extends FactoryBase<TestBigDecimalLiveObject,TestBigDecimal> {
        public final BigDecimalAttribute value = new BigDecimalAttribute(new AttributeMetadata());

        @Override
        protected TestBigDecimalLiveObject createImp(Optional<TestBigDecimalLiveObject> previousLiveObject) {
            return null;
        }
    }

    public static class TestLongDecimal extends FactoryBase<TestBigDecimalLiveObject,TestBigDecimal>{
        public final LongAttribute value = new LongAttribute(new AttributeMetadata());

        @Override
        protected TestBigDecimalLiveObject createImp(Optional<TestBigDecimalLiveObject> previousLiveObject) {
            return null;
        }
    }



    @Test
    public void test_bigdecimal() throws IOException {
        TestBigDecimal testBigDecimal = new TestBigDecimal();
        testBigDecimal.value.set(new BigDecimal("12445.67"));

        WebGuiServer webGuiServer = new WebGuiServer(null,null,0,null,null);
        ObjectMapper objectMapper = webGuiServer.getObjectMapper();

        String data = objectMapper.writeValueAsString(testBigDecimal);
        System.out.println(data);

        TestBigDecimal readed = objectMapper.readValue(data,TestBigDecimal.class);
        readed.reconstructMetadataDeepRoot();
        Assert.assertEquals("12445.67",readed.value.get().toString());



    }

    @Test //JavaScript and long not good
    public void test_long() throws IOException {
        TestLongDecimal testLongDecimal = new TestLongDecimal();
        testLongDecimal.value.set(54564L);

        WebGuiServer webGuiServer = new WebGuiServer(null,null,0,null,null);
        ObjectMapper objectMapper = webGuiServer.getObjectMapper();

        String data = objectMapper.writeValueAsString(testLongDecimal);
        System.out.println(data);

        TestLongDecimal readed = objectMapper.readValue(data,TestLongDecimal.class);
        readed.reconstructMetadataDeepRoot();
        Assert.assertEquals("54564",readed.value.get().toString());



    }


}