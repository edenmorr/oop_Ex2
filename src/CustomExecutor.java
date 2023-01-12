import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomExecutor {
    static final int DEFUALT_MIN_PRIORATY = 0;
    static final int DEFUALT_MAX_PRIORATY = 10;

    ThreadPoolExecutor pool;
    int[] priorities;

    public CustomExecutor(){
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available cores : " + cores);
        pool = new ThreadPoolExecutor(2, 5, 300, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(cores - 1)){
            @Override
            public void execute(Runnable runnable) {
                Task<?> task = (Task<?>)runnable;
                priorities[task.getTaskType().getPriorityValue() - DEFUALT_MIN_PRIORATY]++;
                super.execute(runnable);
            }

            @Override
            protected void afterExecute(Runnable runnable, Throwable t) {
                Task<?> task = (Task<?>)runnable;
                priorities[task.getTaskType().getPriorityValue() - DEFUALT_MIN_PRIORATY]--;
                super.afterExecute(runnable, t);
            }
        };

        priorities = new int[DEFUALT_MAX_PRIORATY - DEFUALT_MIN_PRIORATY];
    }


    public <T> Task<T> submit(Task<T> task){
        pool.execute(task);
        return task;
    }

    public <T> Task<T> submit(Callable<T> callable){
        return submit(Task.createTask(callable));
    }

    public <T> Task<T> submit(Callable<T> callable, TaskType type){
        return submit(Task.createTask(callable, type));
    }

    public int getCurrentMax(){ //O(a) == O(1)
        for(int i = 0; i < priorities.length; i++){
            if(priorities[i] != 0)
                return i + DEFUALT_MIN_PRIORATY;
        }
        return DEFUALT_MAX_PRIORATY;
    }

    public void gracefullyTerminate(){
        pool.shutdown();
    }
}
