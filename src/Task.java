import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Task<T> extends FutureTask<T> implements Comparable<Task<T>>, Callable<T> {
    private TaskType taskType;
    private Callable<T> callable;

    private Task(Callable<T> callable, TaskType type) {
        super(callable);
        this.taskType = type;
        this.callable = callable;///
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task<?> task = (Task<?>) o;
        return taskType == task.taskType && Objects.equals(callable, task.callable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskType, callable);
    }

    private Task(Callable<T> callable) {
        super(callable);
        taskType = TaskType.OTHER;
        this.callable = callable;////
    }

    public static <T> Task<T> createTask(Callable<T> callable) {
        return new Task<T>(callable);
    }

    public static <T> Task<T> createTask(Callable<T> callable, TaskType taskType) {
        return new Task<T>(callable, taskType);
    }

    @Override
    public int compareTo(Task o) {
        return Integer.compare(getTaskType().getPriorityValue(), o.getTaskType().getPriorityValue());
    }

    public Callable<T> getCallable() {
        return callable;
    }

    public void setCallable(Callable<T> callable) {
        this.callable = callable;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    @Override
    public T call() throws Exception {
        try {
            callable.call();
        } catch (Exception e) {
            System.out.println("Error" +e);
            return null;
        }
        return callable.call();
    }
}
