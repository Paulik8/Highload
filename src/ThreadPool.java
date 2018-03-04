import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class ThreadPool implements Executor {

    private final Queue<Runnable> workQueue = new ConcurrentLinkedQueue<>();
    private volatile boolean isRunning = true;
    int currentCount = 0;
    int threadCount = 0;
    public ThreadPool(int nThreads) {
        this.threadCount = nThreads;
    }

    @Override
    public void execute(Runnable command) {
        if (command != null) {
            synchronized (this) {
                if(currentCount<threadCount) {
                    new Thread(new TaskWorker()).start();
                    currentCount++;
                }
                if (!workQueue.offer(command)) System.out.println("Task can not add in Queue");
            }
        }
    }
    public void shutdown() {
        isRunning = false;
    }

    private final class TaskWorker implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                Runnable nextTask;
                synchronized (this) {
                    nextTask = workQueue.poll();
                }
                if (nextTask != null) {
                    nextTask.run();
                } else {
                    synchronized (this) {
                        currentCount--;
                    }
                    return;
                }
            }
        }

    }
}
