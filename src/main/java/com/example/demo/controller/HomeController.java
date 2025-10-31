package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/calc")
    public String calculator() {
        return "calculator";
    }

    @PostMapping("/calc")
    public String calculateResult(
            @RequestParam("num1") double num1,
            @RequestParam("num2") double num2,
            @RequestParam("operation") String operation,
            Model model) {

        double result = 0;
        String operationSymbol = "";

        switch (operation) {
            case "add":
                result = num1 + num2;
                operationSymbol = "+";
                break;
            case "subtract":
                result = num1 - num2;
                operationSymbol = "-";
                break;
            case "multiply":
                result = num1 * num2;
                operationSymbol = "×";
                break;
            case "divide":
                if (num2 != 0) {
                    result = num1 / num2;
                    operationSymbol = "÷";
                } else {
                    model.addAttribute("error", "Деление на ноль невозможно!");
                    return "result";
                }
                break;
        }

        model.addAttribute("num1", num1);
        model.addAttribute("num2", num2);
        model.addAttribute("operation", operationSymbol);
        model.addAttribute("result", result);

        return "result";
    }

    @GetMapping("/converter")
    public String currencyConverter() {
        return "converter";
    }

    @PostMapping("/converter")
    public String convertCurrency(
            @RequestParam("amount") double amount,
            @RequestParam("fromCurrency") String fromCurrency,
            @RequestParam("toCurrency") String toCurrency,
            Model model) {

        // Курсы валют относительно USD
        double fromRate = getExchangeRate(fromCurrency);
        double toRate = getExchangeRate(toCurrency);

        // Конвертация через USD
        double amountInUSD = amount / fromRate;
        double convertedAmount = amountInUSD * toRate;

        model.addAttribute("amount", amount);
        model.addAttribute("fromCurrency", getCurrencyName(fromCurrency));
        model.addAttribute("toCurrency", getCurrencyName(toCurrency));
        model.addAttribute("convertedAmount", String.format("%.2f", convertedAmount));

        return "converter";
    }

    private double getExchangeRate(String currency) {
        switch (currency) {
            case "USD": return 1.0;
            case "EUR": return 0.92;
            case "RUB": return 92.0;
            case "GBP": return 0.79;
            case "JPY": return 149.0;
            case "CNY": return 7.24;
            default: return 1.0;
        }
    }

    private String getCurrencyName(String code) {
        switch (code) {
            case "USD": return "Доллар США";
            case "EUR": return "Евро";
            case "RUB": return "Российский рубль";
            case "GBP": return "Фунт стерлингов";
            case "JPY": return "Японская йена";
            case "CNY": return "Китайский юань";
            default: return code;
        }
    }
}