package iddqd_idkfa.transfer;

import iddqd_idkfa.transfer.api.TransferAPI;
import iddqd_idkfa.transfer.model.Account;
import iddqd_idkfa.transfer.model.Transfer;
import iddqd_idkfa.transfer.repository.Repository;
import io.vertx.core.Vertx;

import java.math.BigDecimal;
import java.util.Currency;

public class Application {

    private static TransferAPI api;
    private static Vertx vertx;
    static {
        api = new TransferAPI(8000);
        vertx = null;
    }

    private static void addDummyData() {
        Account acc1 = new Account("Igor Bogdanoff", new BigDecimal("4567"), Currency.getInstance("EUR"));
        Repository.addAccount(acc1.getId(), acc1);
        Account acc2 = new Account("Grichka Bogdanoff", new BigDecimal("1234"), Currency.getInstance("EUR"));
        Repository.addAccount(acc2.getId(), acc2);
        Account acc3 = new Account("Berkan Denizyaran", new BigDecimal("98765"), Currency.getInstance("GBP"));
        Repository.addAccount(acc3.getId(), acc3);
        Transfer tr1 = new Transfer(0, 1, new BigDecimal("300"), Currency.getInstance("EUR"), "Lip Augmentation costs");
        Repository.addTransfer(tr1.getId(), tr1);
        Transfer tr2 = new Transfer(1, 2, new BigDecimal("200"), Currency.getInstance("USD"), "Rights to 'Hunt Down the Freeman' game");
        Repository.addTransfer(tr2.getId(), tr2);
        Transfer tr3 = new Transfer(1, 0, new BigDecimal("100"), Currency.getInstance("EUR"), "Flat Earth Society Membership");
        Repository.addTransfer(tr3.getId(), tr3);
        Transfer tr4 = new Transfer(0, 1, new BigDecimal("400"), Currency.getInstance("USD"), "Chin implant");
        Repository.addTransfer(tr4.getId(), tr4);
    }

    public static void startBeforeTest() {
        if (vertx != null) {
            return;
        }
        try {
            addDummyData();

            vertx = Vertx.vertx();

            vertx.deployVerticle(api);
            System.out.println("Successfully started server");
        } catch(Exception e) {
            System.err.println("Cannot start server");
        }
    }

    public static void stopAfterTest() {
        vertx.close();
    }

    public static void main(final String[] args) {
        try {
            addDummyData();

            Vertx vertx = Vertx.vertx();

            vertx.deployVerticle(api);
            System.out.println("Successfully started server");
        } catch(Exception e) {
            System.err.println("Cannot start server");
        }
    }
}
