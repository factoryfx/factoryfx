package io.github.factoryfx.example.server.shop;

import io.github.factoryfx.example.server.ServerBuilder;
import io.github.factoryfx.example.server.ServerRootFactory;
import io.github.factoryfx.example.server.testutils.FactoryTreeBuilderRule;
import org.eclipse.jetty.server.Server;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ShopResourceTest {

    private ShopResource shopResource;
    private OrderStorage orderStorage;

    public final FactoryTreeBuilderRule<Server, ServerRootFactory, Void> ctx = new FactoryTreeBuilderRule<>(new ServerBuilder().builder()) {

        {
            shopResource = get(ShopResourceFactory.class);
            orderStorage = get(OrderStorageFactory.class);
        }
    };

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
