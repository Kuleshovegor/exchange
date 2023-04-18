package ru.kuleshov.users.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ExchangeClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String address;

    public ExchangeClient(@Value("${exchange.url}") String address) {
        this.address = address;
    }

    public long getPrice(String companyName) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(address + "/exchange/stock/price?companyName=" + companyName))
                .build();

        return sendRequest(request);
    }

    public long buyStock(String companyName, long stocks, long balance) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(address + "/exchange/stock/buy?companyName=" + companyName
                        + "&count=" + stocks
                        + "&sum=" + balance)
                ).build();

        return sendSellRequest(request);
    }

    public long sellStock(String companyName, long stocks) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(address + "/exchange/stock/sell?companyName=" + companyName + "&count=" + stocks))
                .build();

        return sendRequest(request);
    }

    private Long sendSellRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 400) {
                throw new IllegalArgumentException();
            }
            if (response.statusCode() != 200) {
                throw new IllegalStateException();
            }

            return Long.parseLong(response.body());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException();
        }
    }

    private Long sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException();
            }

            return Long.parseLong(response.body());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException();
        }
    }
}
