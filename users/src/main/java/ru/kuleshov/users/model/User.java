package ru.kuleshov.users.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private final Map<String, Long> nameToStocks = new HashMap<>();
    private long balance = 0;

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getStocks(String nameCompany) {
        Long stocks = nameToStocks.get(nameCompany);

        return stocks == null ? 0 : stocks;
    }

    public Map<String, Long> getNameToStocks() {
        return nameToStocks;
    }

    public void addStock(String nameCompany, long stocks) {
        synchronized (nameToStocks) {
            nameToStocks.putIfAbsent(nameCompany, 0L);
            nameToStocks.put(nameCompany, nameToStocks.get(nameCompany) + stocks);
        }
    }

    public void removeStock(String nameCompany, long stocks) {
        synchronized (nameToStocks) {
            nameToStocks.putIfAbsent(nameCompany, 0L);
            nameToStocks.put(nameCompany, nameToStocks.get(nameCompany) - stocks);
        }
    }
}
