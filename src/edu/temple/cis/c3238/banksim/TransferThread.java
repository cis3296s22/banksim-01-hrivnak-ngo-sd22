package edu.temple.cis.c3238.banksim;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */
class TransferThread extends Thread {

    private final int numTransactions = 1000;
    private final Bank bank;
    private final int fromAccount;
    private final int maxAmount;

    public TransferThread(Bank b, int from, int max) {
        bank = b;
        fromAccount = from;
        maxAmount = max;
    }

    @Override
    public void run() {


        for (int i = 0; i < numTransactions; i++) {

            int toAccount = (int) (bank.getNumAccounts() * Math.random());
            int amount = (int) (maxAmount * Math.random());
            try {
                bank.transfer(fromAccount, toAccount, amount);
            } 
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try{
            Thread.sleep(250);
            bank.test();
            Thread.sleep(250);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        bank.doneTrans();

        System.out.println("------------------------------------------------------------");
        System.out.printf("%-30s Account[%d] has finished with its transactions.\n", Thread.currentThread().toString(), fromAccount);
        System.out.println("Bank : Finished Processing " + numTransactions + " transactions for account number : "+ fromAccount);
        System.out.println("Total Number of Transactions " + bank.getNumTransactions() + " // Account["+fromAccount+"] had " + numTransactions + " transactions");
        bank.closeBank();

        System.exit(0);
    }
}
