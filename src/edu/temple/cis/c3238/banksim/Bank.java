package edu.temple.cis.c3238.banksim;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */

public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long numTransactions = 0;
    private final int initialBalance;
    private final int numAccounts;
    AtomicInteger signal;
    Semaphore semaphore = new Semaphore(1);



    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(i, initialBalance);
        }
        numTransactions = 0;
        signal = new AtomicInteger(0);
    }

    public void transfer(int from, int to, int amount) throws InterruptedException{
        int lessAmount = Math.min( from, to );
        int moreAmount = Math.max( from, to );

        synchronized ( accounts[lessAmount] ) {
            synchronized ( accounts[moreAmount] ) {
                semaphore.acquire();
                if (accounts[from].withdraw(amount)) {
                    accounts[to].deposit(amount);
                    System.out.printf("Account %d successfully transferred $%d to Account %d.\n", from, amount, to);
                }
                else
                    System.out.printf("Transfer of $%d from Account %d to Account %d failed.\n", amount, from, to);

                // Uncomment line when ready to start Task 3.
                if (shouldTest()){

                    test();
                }
                semaphore.release();
            }
        }
    }





    public int getNumAccounts() {
        return numAccounts;
    }


    public boolean shouldTest() {
        return ++numTransactions % NTEST == 0;
    }

    public synchronized void test() {
        while(signal.get() != 0){
            try{
                wait(10);

            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        if(signal.get() == 0){
            new TestingThread( accounts, initialBalance, numAccounts, Thread.currentThread(), semaphore).start();
        }
    notifyAll();
    }
}