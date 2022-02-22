package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */
public class Account {

    private volatile int balance;
    private final int id;
    private Bank bank;
    private int numTransactions;

    public Account(int id, int initialBalance, Bank bank) {
        this.id = id;
        this.balance = initialBalance;
        this.bank = bank;
        numTransactions = 0;
    }

    public int getBalance() {
        return balance;
    }

    public synchronized boolean withdraw(int amount) {
        if (amount <= balance) {
            int currentBalance = balance;
             Thread.yield(); // Try to force collision
            int newBalance = currentBalance - amount;
            balance = newBalance;
            return true;
        } else {
            return false;
        }
    }

    public synchronized void deposit(int amount) {
        int currentBalance = balance;
        Thread.yield();   // Try to force collision
        int newBalance = currentBalance + amount;
        balance = newBalance;
        notifyAll();
    }

    public synchronized void waitForAvailableFunds(int amount){
        while(bank.isOpen() && (amount > balance)){
            System.out.printf("wait- Account %d, Balance %d, Amount %d\n", id, balance, amount);
            try{
                System.out.println("waiting");
                wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void addTransaction(){
        numTransactions++;
    }
    public int getNumTransactions(){
        return numTransactions;
    }
    
    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }
}
