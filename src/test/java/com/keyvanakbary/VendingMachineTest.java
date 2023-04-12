package com.keyvanakbary;

import com.keyvanakbary.VendingMachine.InsufficientFunds;
import com.keyvanakbary.VendingMachine.ItemNotFound;
import com.keyvanakbary.VendingMachine.NotEnoughChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.keyvanakbary.Coin.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VendingMachineTest {
    private VendingMachine machine;

    @BeforeEach
    void setUp() {
        machine = new VendingMachine()
                .configureItem(1, new Item("Chips", 4))
                .configureItem(2, new Item("Coca Cola", 12))
                .configureItem(3, new Item("Chocolate Bar", 15));
    }

    @Test
    void buyingWithoutFundsShouldFail() {
        assertThrows(InsufficientFunds.class, () -> machine.buy(1));
    }

    @Test
    void buyingWithoutChangeShouldFail() {
        machine.insert(List.of(FIVE_CENTS));

        assertThrows(NotEnoughChange.class, () -> machine.buy(1));
    }

    @Test
    void forceBuyingWithoutChangeShouldSucceed() {
        machine.insert(List.of(FIVE_CENTS));

        assertEquals(List.of(), machine.forceBuy(1));
    }

    @Test
    void buyingANonExistingItemShouldFail() {
        assertThrows(ItemNotFound.class, () -> machine.buy(10));
    }

    @ParameterizedTest
    @MethodSource("changeSourceProvider")
    void buyingAnItemShouldReturnChange(List<Coin> insertCoins, int itemCode, List<Coin> expectedChange) {
        machine.configureCoins(ONE_CENT, 100)
                .configureCoins(TWO_CENTS, 100)
                .configureCoins(FIVE_CENTS, 100);

        machine.insert(insertCoins);

        assertEquals(expectedChange, machine.buy(itemCode));
    }

    private static Stream<Arguments> changeSourceProvider() {
        return Stream.of(
                Arguments.of(
                        List.of(FIVE_CENTS, FIVE_CENTS, FIVE_CENTS),
                        2,
                        List.of(TWO_CENTS, ONE_CENT)
                ),
                Arguments.of(
                        List.of(FIVE_CENTS, FIVE_CENTS, FIVE_CENTS),
                        3,
                        List.of()
                ),
                Arguments.of(
                        List.of(FIVE_CENTS, FIVE_CENTS, FIVE_CENTS, TWO_CENTS),
                        2,
                        List.of(FIVE_CENTS)
                )
        );
    }
}
