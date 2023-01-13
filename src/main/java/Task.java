import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Task<T> extends FutureTask<T> implements Comparable<Task<?>>, Callable<T>{
    private TaskType taskType;
    private Callable<T> callable;

    /**
     * private constructor of Task<T>
     * @param callable callable to run on submit/execute
     */
    private Task(Callable<T> callable) {
        super(callable);
        this.taskType = TaskType.OTHER;
        this.callable = callable;
    }

    /**
     * private constructor of Task<T>
     * @param callable callable to run on submit/execute
     * @param type TaskType to assign priority value
     */
    private Task(Callable<T> callable, TaskType type) {
        super(callable);
        this.taskType = type;
        this.callable = callable;
    }
    
    /**
     * check if current object is the same as another
     * @param obj another object
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) //if the same instance return true
            return true;
        if (obj == null || getClass() != obj.getClass()) //if not same type return false
            return false;
        Task<?> task = (Task<?>) obj;
        return taskType == task.getTaskType() && Objects.equals(this, task); //check if object are equal
    }

    /**
     * hash the current callable and tasktype
     * @return calculated hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(taskType, callable);
    }

    /**
     * calls the assigned callable and returns its value
     * @return the result of the callable computation
     */
    @Override
    public T call() throws Exception {
        return callable.call();
    }

    /**
     * run the assignend callable without returning it
     */
    @Override
    public void run() {
        super.run();
    }

    /**
     * crate a Task object from callable
     * @param <T> type of callable/returned task
     * @param callable callable to run on submit/execute
     * @return new task<T> that will run/call the passed callable
     */
    public static <T> Task<T> createTask(Callable<T> callable) {
        return new Task<T>(callable);
    }
    
    /**
     * crate a Task object from callable
     * @param <T> type of callable/returned task
     * @param callable callable to run on submit/execute
     * @param taskType tasktype to assign priority value
     * @return new task<T> that will run/call the passed callable
     */
    public static <T> Task<T> createTask(Callable<T> callable, TaskType taskType) {
        return new Task<T>(callable, taskType);
    }

    /**
     * compare current task to another task based of priority
     * @param o task to compare to
     * @return 0 if equal, 1 if [this] priority is bigger, -1 otherwise
     */
    public int compareTo(Task<?> o) {
        return Integer.compare(getTaskType().getPriorityValue(), o.getTaskType().getPriorityValue());
    }

    /**
     * set new a new callable to this task
     * @param callable new callable to assign
     */
    public void setCallable(Callable<T> callable) {
        this.callable = callable;
    }

    /**
     * @return current callable operation 
     */
    public Callable<T> getCallable() {
        return callable;
    }

    /**
     * @return set new tasktype [priority]
     */
    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    /**
     * @return current tasktype [priority]
     */
    public TaskType getTaskType() {
        return taskType;
    }
}
