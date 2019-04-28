package iddqd_idkfa.transfer.repository;

import iddqd_idkfa.transfer.model.Account;
import iddqd_idkfa.transfer.model.Transfer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Repository {

    private static final Map<Integer, Account> accounts;
    private static final Map<Integer, Transfer> transfers;
    private static final Map<Integer, Boolean> locks;

    static {
        accounts = new LinkedHashMap<>();
        transfers = new LinkedHashMap<>();
        locks = new LinkedHashMap<>();
    }

    public static void addTransfer(Integer k, Transfer v) {
        try {
            transfers.put(k, v);
        } catch (Exception e) {
            System.err.println("Cannot add transfer");
        }
    }

    public static void addAccount(Integer k, Account v) {
        try {
            accounts.put(k, v);
            locks.put(k, false);
        } catch (Exception e) {
            System.err.println("Cannot add account");
        }
    }

    public static Transfer getTransfer(Integer k) {
        try {
            return transfers.get(k);
        } catch (Exception e) {
            System.err.println("Cannot fetch transfer");
        }
        return null;
    }

    public static Account getAccount(Integer k) {
        try {
            return accounts.get(k);
        } catch (Exception e) {
            System.err.println("Cannot fetch account");
        }
        return null;
    }

    public static void removeTransfer(Integer k) {
        try {
            transfers.remove(k);
        } catch (Exception e) {
            System.err.println("Cannot remove transfer");
        }
    }

    public static void removeAccount(Integer k) {
        try {
            accounts.remove(k);
        } catch (Exception e) {
            System.err.println("Cannot remove account");
        }
    }

    public static Collection<Transfer> allTransfers() {
        return transfers.values();
    }

    public static Collection<Account> allAccounts() {
        return accounts.values();
    }

    synchronized public static boolean tryLock(int accountId) {
        if (locks.get(accountId)) {
            System.out.println("Account is locked by another request - try again later: " + accountId);
            return false;
        }
        System.out.println("locking " + accountId + " current: " + locks.get(accountId));
        locks.put(accountId, true);
        return true;
    }

    synchronized public static void unlock(int accountId) {
        System.out.println("unlocking " + accountId + " current: " + locks.get(accountId));
        locks.put(accountId, false);
    }

}
