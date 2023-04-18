package ru.kuleshov.users.service;

import org.springframework.stereotype.Service;
import ru.kuleshov.users.client.ExchangeClient;
import ru.kuleshov.users.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    private final ExchangeClient exchangeClient;

    public UserService(ExchangeClient exchangeClient) {
        this.exchangeClient = exchangeClient;
    }

    public int addNewUser() {
        synchronized (users) {
            int id = users.size();
            users.add(new User());

            return id;
        }
    }

    public void addMoney(int id, long sum) {
        if (sum <= 0) {
            throw new IllegalArgumentException();
        }

        synchronized (users.get(id)) {
            User user = users.get(id);
            user.setBalance(user.getBalance() + sum);
        }
    }

    public long getBalance(int id) {
        synchronized (users.get(id)) {
            User user = users.get(id);

            return user.getBalance();
        }
    }

    public List<StockInfo> getStockInfo(int id) {
        synchronized (users.get(id)) {
            User user = users.get(id);
            List<StockInfo> infos = new ArrayList<>();

            for (Map.Entry<String, Long> entry : user.getNameToStocks().entrySet()) {
                infos.add(new StockInfo(entry.getKey(), entry.getValue(), exchangeClient.getPrice(entry.getKey())));
            }

            return infos;
        }
    }

    public long getSummaryMoney(int id) {
        synchronized (users.get(id)) {
            User user = users.get(id);
            long sum = 0;

            for (Map.Entry<String, Long> entry : user.getNameToStocks().entrySet()) {
                sum += entry.getValue() * exchangeClient.getPrice(entry.getKey());
            }

            return sum + user.getBalance();
        }
    }

    public void buyStocks(int id, String companyName, long stocks) {
        synchronized (users.get(id)) {
            User user = users.get(id);

            long summary = exchangeClient.buyStock(companyName, stocks, user.getBalance());

            user.setBalance(user.getBalance() - summary);
            user.addStock(companyName, stocks);
        }
    }

    public void sellStocks(int id, String companyName, long stocks) {
        synchronized (users.get(id)) {
            User user = users.get(id);

            if (stocks > user.getStocks(companyName)) {
                throw new IllegalArgumentException();
            }

            long summary = exchangeClient.sellStock(companyName, stocks);

            user.setBalance(user.getBalance() + summary);
            user.removeStock(companyName, stocks);
        }
    }

    public static class StockInfo implements Serializable {
        private final String nameCompany;
        private final long stocks;
        private final long price;

        public StockInfo(String nameCompany, long stocks, long price) {
            this.nameCompany = nameCompany;
            this.stocks = stocks;
            this.price = price;
        }

        public String getNameCompany() {
            return nameCompany;
        }

        public long getStocks() {
            return stocks;
        }

        public long getPrice() {
            return price;
        }
    }
}
