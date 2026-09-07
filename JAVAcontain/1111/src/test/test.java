package test;

import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // 1、创建一个默认值的账号进行开户
        BankAccount newBankAccount1 = new BankAccount();
        System.out.println("default balance is "+newBankAccount1.getBalance());
        // 2、创建一个指定金额的账号进行开户
        int money = scanner.nextInt();
        BankAccount newBankAccount2 = new BankAccount(money);
        System.out.println("balance2 is "+newBankAccount2.getBalance());

        // 3、创建一个指定金额的账号进行开户
        money = scanner.nextInt();
        BankAccount newBankAccount3 = new BankAccount(money);
        System.out.println("balance3 is "+newBankAccount3.getBalance());

        // 4、给账号存钱，取钱

        BankAccount newBankAccount4 = new BankAccount();
        System.out.println("balance4 is "+newBankAccount4.getBalance());

        money = scanner.nextInt();
        newBankAccount4.deposit(money);//存钱
        System.out.println("balance4 is "+newBankAccount4.getBalance());

        money = scanner.nextInt();
        newBankAccount4.withdraw(money);//取钱
        System.out.println("balance4 is "+newBankAccount4.getBalance());
    }
}
class BankAccount{
    private int balance;
    public BankAccount()
    {
        this.balance=0;
    }
    public BankAccount(int a)
    {
        this.balance=a;
    }
    public int getBalance()
    {
        return this.balance;
    }
    public void withdraw(int amount)
    {
        if(amount>this.balance) { System.out.println("你的余额不足，请查询余额后输入合法的提取资金");}
        else if(amount<0)
        {
            System.out.println("Invalid Amount");
        } else {
            this.balance=this.balance-amount;
        }
    }
    public void deposit(int amount)
    {
        if(amount<0)
        {
            System.out.println("Invalid Amount");
        }
        else {
            this.balance+=amount;
        }
    }
}

