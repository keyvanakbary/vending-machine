package com.keyvanakbary;

import java.util.*;

public class VendingMachine {
    private Map<Coin, Integer> bank = new HashMap<>();
    private Money money = Money.empty();
    private final Map<Integer, Item> items = new HashMap<>();
    private int amount = 0;

    public VendingMachine configureCoins(Coin coin, int amount) {
        bank.put(coin, amount);
        money.add(coin, amount);

        return this;
    }

    public VendingMachine configureItem(int code, Item item) {
        items.put(code, item);

        return this;
    }

    public void insert(List<Coin> coins) {
        amount = totalCents(coins);
//        amount = money.value();

        // add coins to the bank
        money.add(coins.toArray(Coin[]::new));
//        amount = money.value();
        coins.forEach(coin -> bank.put(coin, bank.getOrDefault(coin, 0) + 1));
    }

    public List<Coin> buy(int itemCode) {
        return buy(itemCode, false);
    }

    public List<Coin> forceBuy(int itemCode) {
        return buy(itemCode, true);
    }

    private List<Coin> buy(int itemCode, boolean keepChange) {
        if (!items.containsKey(itemCode)) {
            throw new ItemNotFound();
        }

        int cents = items.get(itemCode).price();

        if (amount - cents < 0) {
            throw new InsufficientFunds();
        }

        int changeAmount = amount - cents;
        List<Coin> changeCoins = new ArrayList<>();
        Money changeCoins2 = Money.empty();
        Map<Coin, Integer> bankCopy = new HashMap<>(bank);
        Money moneyCopy = Money.from(money);

        for (Coin coin: availableSortedCoins()) {
            int numCoins = bankCopy.getOrDefault(coin, 0);
            while (coin.cents() <= changeAmount && numCoins > 0) {
                changeAmount -= coin.cents();
                numCoins--;
                changeCoins.add(coin);
                changeCoins2.add(coin);
                bankCopy.put(coin, numCoins);
                moneyCopy.remove(coin);
            }
        }

        if (!keepChange && changeAmount != 0) {
            throw new NotEnoughChange();
        }

        // if everything goes well change the state
        bank = bankCopy;
        money = Money.from(moneyCopy);
        amount -= cents;

//        return money.coins();

        return changeCoins;
    }

    private List<Coin> availableSortedCoins() {
        return bank.keySet().stream()
                .sorted((c1, c2) -> Integer.compare(c2.cents(), c1.cents()))
                .toList();
    }

    private static int totalCents(List<Coin> coins) {
        return coins.stream().map(Coin::cents).reduce(0, Integer::sum);
    }

    public static class ItemNotFound extends RuntimeException {}
    public static class InsufficientFunds extends RuntimeException {}
    public static class NotEnoughChange extends RuntimeException {}
}
