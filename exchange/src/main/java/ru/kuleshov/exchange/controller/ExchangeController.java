package ru.kuleshov.exchange.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kuleshov.exchange.model.Company;
import ru.kuleshov.exchange.service.ExchangeService;

@Controller
public class ExchangeController {
    private final ExchangeService service;

    public ExchangeController(ExchangeService service) {
        this.service = service;
    }

    @PostMapping("/exchange/stock/sell")
    public ResponseEntity<Long> sellStocks(
            @NonNull @RequestParam String companyName,
            @RequestParam long count
    ) {
        return ResponseEntity.ok(service.sellStocks(companyName, count));
    }

    @PostMapping("/exchange/stock/buy")
    public ResponseEntity<Long> buyStocks(
            @NonNull @RequestParam String companyName,
            @RequestParam long count,
            @RequestParam long sum
    ) {
        return ResponseEntity.ok(service.buyStocks(companyName, count, sum));
    }

    @PostMapping("/exchange/stock/new-price")
    public ResponseEntity<String> setNewPrice(
            @NonNull @RequestParam String companyName,
            @RequestParam long price
    ) {
        service.setNewPrice(companyName, price);

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/exchange/company/add")
    public ResponseEntity<String> sellStocks(
            @NonNull @RequestParam String companyName,
            @RequestParam long count,
            @RequestParam long price
    ) {
        service.addCompany(companyName, count, price);

        return ResponseEntity.ok("OK");
    }

    @GetMapping("/exchange/company")
    public ResponseEntity<Company> sellStocks(
            @NonNull @RequestParam String companyName
    ) {
        return ResponseEntity.ok(service.getCompanyCopy(companyName));
    }

    @GetMapping("/exchange/stock/price")
    public ResponseEntity<Long> getPrice(
            @NonNull @RequestParam String companyName
    ) {
        return ResponseEntity.ok(service.getPrice(companyName));
    }
}
