package iddqd_idkfa.transfer.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Transfer {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger();

    private final int id;

    private int from;

    private int to;

    private BigDecimal amount;

    private Currency currency;

    private String comment;

    private OperationState status;

    public Transfer(int from, int to, BigDecimal amount, Currency currency, String comment) {
        this.id = ID_COUNTER.get();
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.currency = currency;
        this.comment = comment;
        this.status = OperationState.NEW;
        ID_COUNTER.incrementAndGet();
    }

    public Transfer() {
        this.id = ID_COUNTER.getAndIncrement();
        this.status = OperationState.NEW;
    }

    public int getId() {
        return id;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OperationState getStatus() {
        return status;
    }

    public void setStatus(OperationState status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        return id == transfer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(String.valueOf(id) + String.valueOf(from) + String.valueOf(to));
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", amount=" + amount +
                ", currency=" + currency +
                ", comment='" + comment + '\'' +
                ", status=" + status +
                '}';
    }
}
