package test1_2;
import java.util.concurrent.*;

/**
 * Created by zsj on 2017/6/16.
 * 处理socket的线程池处理类
 * 使用了线程池和消息队列
 */
public class TimeServerHandlerExecutePool {

    private ExecutorService excecutor;

    public TimeServerHandlerExecutePool(int maxPoolSize , int queueSize){
        excecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),maxPoolSize,120L,
                TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(Runnable task){
        excecutor.execute(task);
    }

}
