package ru.kuleshov.users;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kuleshov.users.client.ExchangeClient;
import ru.kuleshov.users.controller.UserController;
import ru.kuleshov.users.service.UserService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@SpringBootTest
class UsersApplicationTests {
    private static String COMPANY = "bmw";
    private static int STOCK_COUNT = 10;
    private static int START_PRICE = 10;

    @ClassRule
    public final static GenericContainer<?> exchange = new GenericContainer<>("exchange:0.0.1-SNAPSHOT")
            .withExposedPorts(8080);
    private ExchangeClient exchangeGateway;
    private UserService userService;

    @BeforeEach
    public void setup() {
        exchange.start();
        exchangeGateway = new ExchangeClient("http://localhost:" + exchange.getMappedPort(8080));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + exchange.getMappedPort(8080)
                        + "/exchange/company/add?companyName=" + COMPANY
                        + "&count=" + STOCK_COUNT
                        + "&price=" + START_PRICE)
                )
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException();
        }
        userService = new UserService(exchangeGateway);
    }

    @AfterEach
    public void stop() {
        exchange.stop();
    }

    @Test
    public void testRegister() {
        int id = userService.addNewUser();
        Assertions.assertEquals(0, id);
        id = userService.addNewUser();
        Assertions.assertEquals(1, id);
    }

    @Test
    public void testAddMoney() {
        int id = userService.addNewUser();

        userService.addMoney(id, 300);
    }

    @Test
    public void testGetAndAddMoney() {
        int id = userService.addNewUser();

        userService.addMoney(id, 300);

        long balance = userService.getBalance(id);
        Assertions.assertEquals(300, balance);
    }

    @Test
    public void testAddNegativeMoney() {
        int id = userService.addNewUser();

        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.addMoney(id, -300));
    }

    @Test
    public void testBuy() {
        int id = userService.addNewUser();
        userService.addMoney(id, 300);
        userService.buyStocks(0, COMPANY, 3);

        Assertions.assertEquals(300 - START_PRICE * 3L, userService.getBalance(id));
        Assertions.assertEquals(List.of(new UserService.StockInfo(COMPANY, 3, 2)), userService.getStockInfo(id));
        Assertions.assertEquals(300, userService.getSummaryMoney(id));
    }

    @Test
    public void testSell() {
        int id = userService.addNewUser();
        userService.addMoney(id, 300);
        userService.buyStocks(id, COMPANY, 3);

        setNewPrice(START_PRICE + 2);

        userService.sellStocks(id, COMPANY, 1);

        Assertions.assertEquals(300 - START_PRICE * 3L + 2, userService.getBalance(id));
        Assertions.assertEquals(List.of(new UserService.StockInfo(COMPANY, 2, 4)), userService.getStockInfo(id));
        Assertions.assertEquals(302, userService.getSummaryMoney(id));
    }

    private void setNewPrice(int price) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + exchange.getMappedPort(8080)
                        + "/exchange/stock/new-price?companyName=" + COMPANY
                        + "&price=" + price)
                )
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException();
        }
    }
}
