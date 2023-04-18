package ru.kuleshov.exchange.service;

import org.springframework.stereotype.Service;
import ru.kuleshov.exchange.model.Company;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExchangeService {
    private final Map<String, Company> nameToCompany =  new ConcurrentHashMap<>();

    public long getPrice(String companyName) {
        return nameToCompany.get(companyName).getPrice();
    }

    public Company getCompanyCopy(String companyName) {
        synchronized (nameToCompany.get(companyName)) {
            Company company = nameToCompany.get(companyName);

            return new Company(companyName, company.getStocks(), company.getPrice());
        }
    }

    public void setNewPrice(String companyName, long newPrice) {
        synchronized (nameToCompany.get(companyName)) {
            nameToCompany.get(companyName).setPrice(newPrice);
        }
    }

    public long buyStocks(String companyName, long stocks, long summary) {
        synchronized (nameToCompany.get(companyName)) {
            Company company = nameToCompany.get(companyName);

            if (company.getStocks() < stocks) {
                throw new IllegalArgumentException();
            }

            if (stocks * company.getPrice() > summary) {
                throw new IllegalArgumentException();
            }

            company.buyStocks(stocks);

            return stocks * company.getPrice();
        }
    }


    public long sellStocks(String companyName, long stocks) {
        synchronized (nameToCompany.get(companyName)) {
            Company company = nameToCompany.get(companyName);
            company.sellStocks(stocks);

            return stocks * company.getPrice();
        }
    }

    public void addCompany(String companyName, long stocks, long price) {
        synchronized (nameToCompany) {
            if (nameToCompany.containsKey(companyName)) {
                throw new IllegalArgumentException();
            }

            nameToCompany.put(companyName, new Company(companyName, stocks, price));
        }
    }
}
