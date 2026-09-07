package 线程池;

import java.util.concurrent.Callable;

public class mycallable implements Callable<String> {
    private String name;
    public mycallable(String name) { this.name = name;}
    @Override
    public String call() throws Exception {
        return name;
    }
}
