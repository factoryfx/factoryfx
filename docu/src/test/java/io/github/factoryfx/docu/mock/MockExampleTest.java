package io.github.factoryfx.docu.mock;

import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MockExampleTest {

    @Test
    public void mock_example(){
        FactoryTreeBuilder<Printer, PrinterFactory> builder = new FactoryTreeBuilder<>(PrinterFactory.class, ctx->{
            PrinterFactory factory = new PrinterFactory();
            factory.text.set("Hello World");
            return factory;
        });

        Printer printer = builder.microservice(
                printerFactory -> printerFactory.utility().mock(root -> {
                    Printer mock = Mockito.mock(Printer.class);
                    Mockito.when(mock.print()).thenReturn("mocked");
                    return mock;
                })
        ).build().start();

        Assertions.assertEquals("mocked",printer.print());
    }
}
