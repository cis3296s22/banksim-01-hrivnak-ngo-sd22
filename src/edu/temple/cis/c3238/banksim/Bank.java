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
    public int donetrans = 0;
    AtomicInteger signal;
    Semaphore semaphore = new Semaphore(1);
    private boolean open = true;



    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(i, initialBalance, this);
        }
        numTransactions = 0;
        signal = new AtomicInteger(0);
    }

    public void transfer(int from, int to, int amount) throws InterruptedException{
        int lessAmount = Math.min( from, to );
        int moreAmount = Math.max( from, to );



        //check if account needs to wait for more funds
        accounts[from].waitForAvailableFunds(amount);

                 increment();

                semaphore.acquire();
                if (accounts[from].withdraw(amount)) {
                    accounts[to].deposit(amount);
                    System.out.printf("Account %d successfully transferred $%d to Account %d.\n", from, amount, to);
                    decrement();
                    semaphore.release();

                }
                else
                    System.out.printf("Bank Closed: Transfer of $%d from Account %d to Account %d failed\n", amount, from, to);

                // Uncomment line when ready to start Task 3.
                if (shouldTest()){

                    test();
                }



    }


    void closeBank(){
        synchronized (this){
            open = false;
        }
        for(Account account: accounts){
            synchronized(account){
                account.notifyAll();
            }
        }
    }

    synchronized boolean isOpen(){
        return open;
    }

    public long getNumTransactions(){
        return numTransactions;
    }


    public int getNumAccounts() {
        return numAccounts;
    }

    synchronized void increment(){
        signal.incrementAndGet();
    }
    synchronized void decrement(){
        signal.decrementAndGet();
    }

    public boolean shouldTest() {
        return ++numTransactions % NTEST == 0;
    }

    public int doneTrans(){
        donetrans = 1;
        return donetrans;
    }

    public synchronized void test() {
        while(signal.get() != 0 && isOpen()){
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