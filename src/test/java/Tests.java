import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.concurrent.*;

public class Tests {
    public static final Logger logger = LoggerFactory.getLogger(Tests.class);

    /**
     * Test from given pdf
     */
    @Test
    public void partialTest() {
        CustomExecutor customExecutor = new CustomExecutor();
        var task = Task.createTask(() -> {
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
            }
            return sum;
        }, TaskType.COMPUTATIONAL);
        var sumTask = customExecutor.submit(task);
        final int sum;
        try {
            sum = sumTask.get(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        logger.info(() -> "Sum of 1 through 10 = " + sum);
        Callable<String> callable2 = () -> {
            StringBuilder sb = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            return sb.reverse().toString();
        };
        var priceTask = customExecutor.submit(() -> {
            return 1000 * Math.pow(1.02, 5);
        }, TaskType.COMPUTATIONAL);
        var reverseTask = customExecutor.submit(callable2, TaskType.IO);
        final Double totalPrice;
        final String reversed;
        try {
            totalPrice = priceTask.get();
            reversed = reverseTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        logger.info(() -> "Reversed String = " + reversed);
        logger.info(() -> String.valueOf("Total Price = " + totalPrice));
        logger.info(() -> "Current maximum priority = " + customExecutor.getCurrentMax());

        customExecutor.gracefullyTerminate();
    }

    /**
     * Test all files read the same amount of lines
     */
    @Test
    public void part1EqualReadingTest() {
        String[] files = Ex2_1.createTextFiles(10, 10, 1000);
        final int syncline = Ex2_1.getNumOfLines(files);
        final int threadsline = Ex2_1.getNumOfLinesThreads(files);
        final int poolline = Ex2_1.getNumOfLinesThreadPool(files);

        // check line count is equal
        logger.info(() -> "single thread total lines = " + syncline);
        logger.info(() -> "multiple threads total lines = " + threadsline);
        logger.info(() -> "thread pool total lines = " + poolline);

        assertEquals(syncline, threadsline);
        assertEquals(threadsline, poolline);

        Ex2_1.deleteFiles(files); // clear files
    }

    /**
     * Test all files read time
     */
    @Test
    public void part1TestReadTimes() {
        // we already print the reading time, but here we will print
        // the total time, including setup time.
        String[] files = Ex2_1.createTextFiles(100, 25, 1000);

        long part1 = System.currentTimeMillis();
        final int syncline = Ex2_1.getNumOfLines(files);
        long part2 = System.currentTimeMillis();
        final int threadsline = Ex2_1.getNumOfLinesThreads(files);
        long part3 = System.currentTimeMillis();
        final int poolline = Ex2_1.getNumOfLinesThreadPool(files);
        long part4 = System.currentTimeMillis();

        // check line count is equal
        assertEquals(syncline, threadsline);
        assertEquals(threadsline, poolline);

        // calculate time per part
        long synctime = part2 - part1;
        long threadstime = part3 - part2;
        long pooltime = part4 - part3;

        logger.info(() -> "single thread time = " + synctime);
        logger.info(() -> "multiple threads time = " + threadstime);
        logger.info(() -> "thread pool time = " + pooltime);

        /*
         * results may very, espesically with thread array
         * [usually:
         * 1 thread : ~29ms
         * thread array : ~6ms
         * thread pool : ~8ms
         * ]
         * some runs as expected - the thread pool was fater then the thread array
         * but most times thread array was aster then thread pool, this is because
         * we assign the thread pool size (as instucted in the pdf) to be the same
         * size as the number of files. if we will assign the thread pool size in
         * accordance with the available cores the thread pool should be faster.
         * we will use a new function we created just for this test.
         */

        int cores = Runtime.getRuntime().availableProcessors();

        part1 = System.currentTimeMillis();
        final int syncline2 = Ex2_1.getNumOfLines(files);
        part2 = System.currentTimeMillis();
        final int threadsline2 = Ex2_1.getNumOfLinesThreads(files);
        part3 = System.currentTimeMillis();
        final int poolline2 = Ex2_1.getNumOfLinesThreadPool(files, cores - 1);
        part4 = System.currentTimeMillis();

        // check line count is equal
        assertEquals(syncline2, threadsline2);
        assertEquals(threadsline2, poolline2);

        // calculate time per part
        long synctime2 = part2 - part1;
        long threadstime2 = part3 - part2;
        long pooltime2 = part4 - part3;

        logger.info(() -> "second run : single thread time = " + synctime2);
        logger.info(() -> "second run : multiple threads time = " + threadstime2);
        logger.info(() -> "second run : thread pool time = " + pooltime2);

        /*
         * after this change, most runs the thread pool are faster then the thread array
         * [usually:
         * 1 thread : ~20ms
         * thread array : ~8ms
         * thread pool : ~4ms
         * ]
         * making the same change on the first reading will affect it the same way, but
         * some times the thread array will still be faster, probably because
         * initilizations
         * that happen in the background in the thread pool
         */

        Ex2_1.deleteFiles(files); // clear files
    }

    /**
     * test tasks execute in the correct order
     */
    @Test
    public void testExecutionOrder() {
        /*
         * for this test we will limit the thread pool size to 1
         * and the queue len to some arbitrary number, 40 in this case,
         * then we will add a delay of 2 seconds and in the meantime
         * add severl task in incorrect oreder, we will then see if the
         * CustomExecutor will arrange them correctly based on priority
         * 
         * we will make each task return its finished execution time 
         */

        CustomExecutor ce = new CustomExecutor(1, 40);

        Callable<Integer> deley = () -> {
            Thread.sleep(2000, 0);
            return 1;
        };

        Callable<Long> callable1 = () -> {
            Thread.sleep(100);
            return System.currentTimeMillis();
        };

        Task<Long> task1 = Task.createTask(
                () -> {
                    Thread.sleep(100);
                    return System.currentTimeMillis();
                }, TaskType.IO);

        Task<Long> task2 = Task.createTask(
                () -> {
                    Thread.sleep(100);
                    return System.currentTimeMillis();
                });

        // we will first submit the delet to let the other
        // threads catch up to submittion
        ce.submit(deley);

        // we will when submit the lowest priority first [by defualy type is OTHER]
        // our objective is to see it becomoe the last
        Task<Long> ret1 = ce.submit(task2);

        // we will then submit the second lowest priority
        Task<Long> ret2 = ce.submit(task1);

        // at last we will submit the highest priority
        Task<Long> ret3 =ce.submit(callable1, TaskType.COMPUTATIONAL);
        
        // we expect the tasks to change their order in the queue and
        // execute in the correct assigned priority, should be 
        // ret3 -> ret2 -> ret1

        try { //let all tasks lime to finish
            Thread.sleep(500);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        final Long ret1out;
        final Long ret2out;
        final Long ret3out;
        try {
            ret1out = ret1.get();
            ret2out = ret2.get();
            ret3out = ret3.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        logger.info(() -> "ret3 time : " + ret3out);
        logger.info(() -> "ret2 time : " + ret2out);
        logger.info(() -> "ret1 time : " + ret1out);
        
        //summary + our results
        //we will see that the ms time for ret3 will
        //be the smallest, hence it was executed first [1673585303151]
        //ret2 will be second, hence its was exectuted after 1 [1673585303259]
        //ret3 will be last, so it was executed last [1673585303369]
        //hecne the order was : computational->io->other even though we
        //submmited them in the wrong order, so the priority ordering worked

        // close custom executor
        ce.gracefullyTerminate();
    }
}