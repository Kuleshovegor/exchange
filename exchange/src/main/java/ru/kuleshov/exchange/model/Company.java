package ru.kuleshov.exchange.model;

import java.util.Objects;

public class Company {
    private final String name;
    private long stocks;
    private long price;

    public Company(String name, long stocks, long price) {
        this.name = name;
        this.stocks = stocks;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public long getStocks() {
        return stocks;
    }

    public void buyStocks(long stocks) {
        this.stocks -= stocks;
    }

    public void sellStocks(long stocks) {
        this.stocks += stocks;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(name, company.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
