package lock;

public class withdrawthread extends Thread {
    private Account account;
    public withdrawthread(String name, Account account) {
        super(name);
        this.account = account;

    }
    //线程任务
    @Override
    public void run() {
        account.withdraw(100000);
    }

}
