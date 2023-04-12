package com.keyvanakbary;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Money {
    private final Map<Coin, Integer> coinAmounts;

    public static Money from(Coin ...coins) {
        return new Money(coins);
    }

    public static Money from(Money money) {
        return new Money(money.coinAmounts);
    }
    private Money(Map<Coin, Integer> coinAmounts) {
        this.coinAmounts = new HashMap<>(coinAmounts);
    }

    private Money(Coin ...coins) {
        this.coinAmounts = new HashMap<>();
        add(coins);
    }

    public static Money empty() {
        return new Money();
    }
    public void add(Coin ...coins) {
        for (Coin coin: coins) {
            add(coin, 1);
        }
    }

    public void add(Coin c, int amount) {
        coinAmounts.put(c, coinAmounts.getOrDefault(c, 0) + amount);
    }


    public int value() {
        return coinAmounts.entrySet().stream()
                .map(e -> e.getKey().cents() * e.getValue())
                .reduce(0, Integer::sum);
    }

    public void remove(Coin coin) {
        int amount = coinAmounts.getOrDefault(coin, 0);

        if (amount == 0) {
            throw new RuntimeException("Cannot remove unexisting coin");
        }

        coinAmounts.put(coin, amount - 1);
    }

    public List<Coin> coins() {
        return coinAmounts.entrySet().stream()
                .map(e -> {
                    List<Coin> coins = new ArrayList<>();
                    for (int i = 0; i < e.getValue(); i++) {
                        coins.add(e.getKey());
                    }
                    return coins;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
