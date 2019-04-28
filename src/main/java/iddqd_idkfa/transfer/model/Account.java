package iddqd_idkfa.transfer.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Account {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger();

    private final int id;

    private String name;

    private BigDecimal balance;

    private Currency currency;

    public Account(String name, BigDecimal balance, Currency currency) {
        this.id = ID_COUNTER.get();
        this.name = name;
        this.balance = balance;
        this.currency = currency;
        ID_COUNTER.incrementAndGet();
    }

    public Account() {
        this.id = ID_COUNTER.getAndIncrement();
    }

    public void withdraw(BigDecimal amount) {
        this.balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.balance = balance.add(amount);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return id == account.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(String.valueOf(id) + String.valueOf(name) + String.valueOf(currency));
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", currency=" + currency +
                '}';
    }
}
