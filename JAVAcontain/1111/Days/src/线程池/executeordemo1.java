package 线程池;

import java.awt.*;
import java.util.concurrent.*;

public class executeordemo1 {
    public static void main(String[] args) {
        //创建线程池
        ExecutorService executorService = new ThreadPoolExecutor(3, 5,
                1000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(3)
        , Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());//
        MyRunable myRunable = new MyRunable();
        executorService.execute(myRunable);//提交任务, 创建线程执行
        executorService.execute(myRunable);//提交任务, 创建线程执行
        executorService.execute(myRunable);//提交任务,创建线程执行
        executorService.execute(myRunable);//复用线程执行
        executorService.execute(myRunable);//复用线程执行
        executorService.execute(myRunable);//复用线程执行任务队列也满了
//        executorService.shutdown();//等待所有 任务执行完毕，停止接受新任务
//        executorService.shutdownNow();//停止接受新任务，并停止正在执行的任务,立即执行中断
        //当核心线程在忙时，并且队列已经满了，并且有新的任务进来，就会创建临时线程执行
        executorService.execute(myRunable);
        executorService.execute(myRunable);//创建临时线程执行,临时线程执行完毕，就会回收
        executorService.execute(myRunable);//创建临时线程执行,忙线核心线程数已经满了，临时线程数已经满了，就会拒绝
        executorService.execute(myRunable);
        executorService.execute(myRunable);
//        CallerRunsPolicy会用调用者所在的线程执行线程
        //callerRunsPolicy的线程池的使用, 线程池使用

        Future<String> feature = executorService.submit(new mycallable("callerRunsPolicy"));
        try {
            String result = feature.get();//获取结果
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
