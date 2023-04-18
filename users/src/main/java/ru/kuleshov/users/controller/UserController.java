package ru.kuleshov.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kuleshov.users.service.UserService;

import java.util.List;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/registry")
    public ResponseEntity<Integer> buyStocks() {
        return ResponseEntity.ok(userService.addNewUser());
    }

    @PostMapping("/user/add-money")
    public ResponseEntity<String> addMoney(
            @RequestParam int userId,
            @RequestParam long addedMoney
    ) {
        userService.addMoney(userId, addedMoney);

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/user/get-money")
    public ResponseEntity<Long> getMoney(
            @RequestParam int userId
    ) {
        return ResponseEntity.ok(userService.getBalance(userId));
    }

    @PostMapping("/user/get-summary-money")
    public ResponseEntity<Long> getSummaryMoney(
            @RequestParam int userId
    ) {
        return ResponseEntity.ok(userService.getSummaryMoney(userId));
    }

    @PostMapping("/user/stock/buy")
    public ResponseEntity<String> buyStocks(
            @RequestParam int userId,
            @NonNull @RequestParam String companyName,
            @RequestParam long stock
    ) {
        userService.buyStocks(userId, companyName, stock);

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/user/stock/sell")
    public ResponseEntity<String> sellStocks(
            @RequestParam int userId,
            @NonNull @RequestParam String companyName,
            @RequestParam long stock
    ) {
        userService.sellStocks(userId, companyName, stock);

        return ResponseEntity.ok("OK");
    }

    @GetMapping("/user/stock-info")
    public ResponseEntity<List<UserService.StockInfo>> getStocksInfo(@RequestParam int userId) {
        return ResponseEntity.ok(userService.getStockInfo(userId));
    }
}
