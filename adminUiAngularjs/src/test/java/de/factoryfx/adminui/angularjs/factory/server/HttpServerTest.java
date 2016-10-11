package de.factoryfx.adminui.angularjs.factory.server;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.BigDecimalAttribute;
import de.factoryfx.data.attribute.types.LongAttribute;
import org.junit.Assert;
import org.junit.Test;

public class HttpServerTest {

    public static class TestBigDecimal extends Data {
        public final BigDecimalAttribute value = new BigDecimalAttribute(new AttributeMetadata());

        @Override
        public Object getId() {
            return 1;
        }

        @Override
        public void setId(Object object) {

        }

    }

    public static class TestLongDecimal extends Data {
        public final LongAttribute value = new LongAttribute(new AttributeMetadata());

        @Override
        public Object getId() {
            return 2;
        }

        @Override
        public void setId(Object object) {

        }
    }


    @Test
    public void test_bigdecimal() throws IOException {
        TestBigDecimal testBigDecimal = new TestBigDecimal();
        testBigDecimal.value.set(new BigDecimal("12445.67"));

        HttpServer httpServer = new HttpServer(null,null,0,null,null);
        ObjectMapper objectMapper = httpServer.getObjectMapper();

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

        HttpServer httpServer = new HttpServer(null,null,0,null,null);
        ObjectMapper objectMapper = httpServer.getObjectMapper();

        String data = objectMapper.writeValueAsString(testLongDecimal);
        System.out.println(data);

        TestLongDecimal readed = objectMapper.readValue(data,TestLongDecimal.class);
        readed.reconstructMetadataDeepRoot();
        Assert.assertEquals("54564",readed.value.get().toString());



    }


}