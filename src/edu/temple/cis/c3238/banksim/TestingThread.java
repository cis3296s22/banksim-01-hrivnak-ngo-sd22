package edu.temple.cis.c3238.banksim;

import java.util.concurrent.Semaphore;

public class TestingThread extends Thread{
    private final Account[] accounts;
    private final int initialBalance;
    private final int numAccounts;
    private Thread transferThread;
    private final Semaphore semaphore;




    public TestingThread( Account[] accounts, final int intialBalance, final int numAccounts, Thread transferThread, Semaphore semaphore) {
        this.accounts = accounts;
        this.initialBalance = intialBalance;
        this.numAccounts = numAccounts;
        this.transferThread = transferThread;
        this.semaphore = semaphore;

    }

    @Override
    public void run() {




        try {
            semaphore.acquire();
            System.out.println("-- TESTING THREAD --");
            int totalBalance = 0;
            for (Account account : accounts) {
                System.out.printf("%-30s %s%n",
                        transferThread.toString(), account.toString());
                totalBalance += account.getBalance();
            }
            System.out.printf("%-30s Total balance: %d\n", transferThread.toString(), totalBalance);
            if (totalBalance != numAccounts * initialBalance) {
                System.out.printf("%-30s Total balance changed!\n", transferThread.toString());
                System.exit(0);
            }
            else {
                System.out.printf("%-30s Total balance unchanged.\n", transferThread.toString());
            }
            semaphore.release();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}