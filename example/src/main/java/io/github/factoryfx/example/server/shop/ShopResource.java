package io.github.factoryfx.example.server.shop;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Path("shop")
public class ShopResource {

    private final List<Product> products;
    private final OrderStorage orderStorage;

    public ShopResource(List<Product> products, OrderStorage orderStorage) {
        this.products = products;
        this.orderStorage = orderStorage;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index(){
        try (InputStream inputStream = getClass().getResourceAsStream("/webapp/index.html")){
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("products")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getProducts(){
        return products;
    }

    public static class BuyProductRequest{
        public String productName;
        public String userName;
    }

    @POST
    @Path("buy")
    @Consumes(MediaType.APPLICATION_JSON)
    public void buyProducts(BuyProductRequest buyProductRequest){
        products.stream().filter(p->buyProductRequest.productName.equals(p.getName())).findAny().ifPresent(product -> {
            orderStorage.storeOrder(new OrderStorage.Order(buyProductRequest.userName,product.getPrice(),product.getName(), new Date()));
        });
    }
}
