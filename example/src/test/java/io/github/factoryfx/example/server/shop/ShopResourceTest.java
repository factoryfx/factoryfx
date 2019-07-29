package io.github.factoryfx.example.server.shop;

import io.github.factoryfx.example.server.ServerBuilder;
import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.example.server.testutils.FactoryTreeBuilderRule;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ShopResourceTest {

    private ShopResource shopResource;
    private OrderStorage orderStorage;

    @RegisterExtension
    public final FactoryTreeBuilderRule<Server, ServerRootFactory> ctx = new FactoryTreeBuilderRule<>(new ServerBuilder().builder(), rule -> {
            shopResource = rule.get(ShopResourceFactory.class);
            orderStorage = rule.get(OrderStorageFactory.class);
    });

    @Test
    public void testShopResource() {

        ShopResource.BuyProductRequest buyProductRequest = new ShopResource.BuyProductRequest();
        buyProductRequest.productName = "Car";
        buyProductRequest.userName = "testuser";

        shopResource.buyProducts(buyProductRequest);

        OrderStorage.Order order = orderStorage.getOrders().stream()
                .filter(o -> o.customerName.equals("testuser"))
                .findFirst()
                .get();

        assertThat("product should be 'Car'", order.productName, is("Car"));
    }
}
