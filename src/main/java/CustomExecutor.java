import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;

public class CustomExecutor {
    static final int EMPTY_QUEUE_PRIORITY = -1; //defualt max priority if pool is empty
    static final int DEFUALT_MIN_PRIORATY = 1; //defualt min existing priority
    static final int DEFUALT_MAX_PRIORATY = 10; //defualt max existing priority

    ThreadPoolExecutor pool; //priority sorted pool

    /**
     * used to save how many thread of each prioroty there are int the 
     * current pool + pool queue, each time a thread is added with priority [x]
     * priorities [x - min_priority] will count 1
     * and each time a thread is removed it will subtract 1
     * this array will be used to find the highest priority thread in the pool
    */
    int[] priorities; 

    public CustomExecutor(){
        int cores = Runtime.getRuntime().availableProcessors(); //get number of available cores
        System.out.println("Available cores : " + cores); 
        pool = new ThreadPoolExecutor(cores / 2, cores - 1, 300, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(cores - 1)){
            /**
             * calculate and add to the array the priority of the added Runnable
             */
            @Override
            public void execute(Runnable runnable) {
                Task<?> task = (Task<?>)runnable;
                priorities[task.getTaskType().getPriorityValue() - DEFUALT_MIN_PRIORATY]++;
                super.execute(runnable);
            }

            /**
             * calculate and subracrt from the array the priority of the finished Runnable
             */
            @Override
            protected void afterExecute(Runnable runnable, Throwable t) {
                Task<?> task = (Task<?>)runnable;
                priorities[task.getTaskType().getPriorityValue() - DEFUALT_MIN_PRIORATY]--;
                super.afterExecute(runnable, t);
            }
        };

        //create new priority array with size of [DEFUALT_MAX_PRIORATY - DEFUALT_MIN_PRIORATY]
        priorities = new int[DEFUALT_MAX_PRIORATY - DEFUALT_MIN_PRIORATY];
    }

    public CustomExecutor(int cores, int queueLen){
        System.out.println("Available cores : " + cores); 
        pool = new ThreadPoolExecutor(cores, cores, 300, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(queueLen)){
            /**
             * calculate and add to the array the priority of the added Runnable
             */
            @Override
            public void execute(Runnable runnable) {
                Task<?> task = (Task<?>)runnable;
                priorities[task.getTaskType().getPriorityValue() - DEFUALT_MIN_PRIORATY]++;
                super.execute(runnable);
            }

            /**
             * calculate and subracrt from the array the priority of the finished Runnable
             */
            @Override
            protected void afterExecute(Runnable runnable, Throwable t) {
                Task<?> task = (Task<?>)runnable;
                priorities[task.getTaskType().getPriorityValue() - DEFUALT_MIN_PRIORATY]--;
                super.afterExecute(runnable, t);
            }
        };

        //create new priority array with size of [DEFUALT_MAX_PRIORATY - DEFUALT_MIN_PRIORATY]
        priorities = new int[DEFUALT_MAX_PRIORATY - DEFUALT_MIN_PRIORATY];
    }

    /**
     * submit new task to excecute sometime in the 
     * future when the queue clears up
     * @param <T> return type of given task
     * @param task task<?> object to run
     * @return Task used to get data after execution of [task]
     */
    public <T> Task<T> submit(Task<T> task){
        pool.execute(task);
        return task;
    }

    /**
     * submit new callable to compute sometime in the 
     * future when the queue clears up
     * @param <T> return type of given callable
     * @param callable Callable object to run
     * @return Task used to get data after execution of [callable]
     */
    public <T> Task<T> submit(Callable<T> callable){
        return submit(Task.createTask(callable));
    }

    /**
     * submit new callable to compute sometime in the 
     * future when the queue clears up and assign it a priority
     * @param <T> return type of given callable
     * @param callable Callable object to run
     * @param type TaskType that will determite priority
     * @return Task used to get data after execution of [callable]
     */
    public <T> Task<T> submit(Callable<T> callable, TaskType type){
        return submit(Task.createTask(callable, type));
    }

    /**
     * Calculate current max priority in queue/pool
     * @return Calculated priority value, -1 [EMPTY_QUEUE_PRIORITY] if queue was empty
     */
    public int getCurrentMax(){ //O(a) == O(1)
        for(int i = 0; i < priorities.length; i++){
            if(priorities[i] != 0)
                return i + DEFUALT_MIN_PRIORATY;
        }
        return EMPTY_QUEUE_PRIORITY;
    }

    /**
     * terminate current pool 
     * (+) wait for all tasks in queue/pool to finish 
     * (+) decline adding new tasks 
     */
    public void gracefullyTerminate(){
        pool.shutdown();
    }
}