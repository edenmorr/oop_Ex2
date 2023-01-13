import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.*;
import java.io.IOException;
import java.util.concurrent.*;

public class Ex2_1 {
    /**
     * Line counter that implements Callable, used to submit to thread pool
     */
    public static class LineCounterCallable implements Callable<Integer> {
        private String fileName;
        private LineCoutner lineCoutner;

        public LineCounterCallable(String fileName) {
            lineCoutner = new LineCoutner();
            this.fileName = fileName;
        }

        /**
         * count number of lines in current file and return it
         */
        @Override
        public Integer call() throws Exception {
            lineCoutner.count(fileName);
            return lineCoutner.getLineCount();
        }

        /**
         * @return total number of lines in the last proccessed file
         */
        public int getLineCount() {
            return lineCoutner.getLineCount();
        }

        /**
         * @param fileName set path of the file that will be read
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * @param fileName get path of the file that will be read
         */
        public String getFileName() {
            return fileName;
        }
    }

    /**
     * Line counter that implements Thread, used to run as a signgle thread file reader
     */
    public static class LineCounterThread extends Thread {
        private String fileName;
        private LineCoutner lineCoutner;

        public LineCounterThread(String fileName) {
            this.fileName = fileName;
            lineCoutner = new LineCoutner();
        }

        /**
         * count number of lines in current file
         */
        @Override
        public void run() {
            lineCoutner.count(fileName);
        }

        /**
         * @return total number of lines in the last proccessed file
         */
        public int getLineCount() {
            return lineCoutner.getLineCount();
        }

        /**
         * @param fileName set path of the file that will be read
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * @param fileName get path of the file that will be read
         */
        public String getFileName() {
            return fileName;
        }
    }

    /**
     * Line counter that reads number of lines in file and returns it
     */
    public static class LineCoutner {
        private int countLines;

        public LineCoutner() {
            this.countLines = 0;
        }

        /**
         * Count number of lines in file
         * @param fileName path of the file that will be read
         */
        public void count(String fileName) {
            int count = 0;
            //create file reader and count every line in a given file path [fileName]
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                while (reader.readLine() != null) {
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            countLines = count;
        }

        /**
         * @return total number of lines in the last proccessed file
         */
        public int getLineCount() {
            return countLines;
        }
    }

    /**
     * Creates text files in current project directory
     * @param n number of text files
     * @param seed random seed to generate number of lines in files
     * @param bound max number of lines in each file
     * @return paths to created txt files
     */
    public static String[] createTextFiles(int n, int seed, int bound) {
        String[] paths = new String[n];
        Random rnd = new Random(seed);

        //for i=0-[n-1] generate new file named "file_i"
        for (int i = 0; i < n; i++) {
            String filepath = "file_" + i + ".txt";
            int linecount = rnd.nextInt(bound);
            //add current file path to retured array
            paths[i] = filepath;
            
            if (!createFile(filepath)) { //check if file was created successfuly
                System.out.println("Error creating file. Name : " + filepath);
            } else if (!writeLinesToFile(filepath, linecount)) { //check if data was written successfuly to file
                System.out.println("Error writing to file. Name : " + filepath);
            }
        }

        return paths;
    }

    /**
     * Create a single txt file
     * @param filepath path of new txt file
     * @return true if created successfuly/already exists, false otherwise
     */
    private static boolean createFile(String filepath) {
        try {
            //create new file in path [filepath]
            File myObj = new File(filepath);
            myObj.createNewFile();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * write lines to file
     * @param filepath path of file
     * @param count number of lines to write
     * @return true if lines were written successfuly, false otherwise
     */
    private static boolean writeLinesToFile(String filepath, int count) {
        try {
            //create file write that will write to [filepath] a given number of lines [count]
            FileWriter myWriter = new FileWriter(filepath);
            for (int i = 0; i < count; i++) {
                if (i != 0)
                    myWriter.write('\n');
                myWriter.write("Line number : " + (i + 1));
            }
            myWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * reads number of total lines in given files, using one thread
     * @param fileNames files to read
     * @return combined line count in all files
     */
    public static int getNumOfLines(String[] fileNames) {
        int totalLineCount = 0;
        LineCoutner lineCoutner = new LineCoutner();

        long start = System.currentTimeMillis(); //start counting reading time

        //read total number of lines in given files [fileNames] using LineCounter
        for (String fileName : fileNames) {
            lineCoutner.count(fileName);
            totalLineCount += lineCoutner.getLineCount();
        }

        long end = System.currentTimeMillis(); //stop counting reading time

        System.out.println("time = " + (end - start) + " ms"); //print total reading time
        return totalLineCount;
    }

    /**
     * reads number of total lines in given files, using several threads (array)
     * @param fileNames files to read
     * @return combined line count in all files
     */
    public static int getNumOfLinesThreads(String[] fileNames) {
        int lines = 0;
        LineCounterThread[] threads = new LineCounterThread[fileNames.length]; //one thread for each file

        long start = System.currentTimeMillis(); //start couting reading time

        try {
            //create a reading thread for each file and start it
            for (int i = 0; i < fileNames.length; i++) {
                threads[i] = new LineCounterThread(fileNames[i]);
                threads[i].start();
            }
            //wait for all thraed to finish and count total line count
            for (int i = 0; i < threads.length; i++) {
                if (!threads[i].isAlive()) {
                    lines += threads[i].getLineCount();
                } else {
                    threads[i].join();
                    lines += threads[i].getLineCount();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis(); //stop counting reading time
        System.out.println("time = " + (end - start) + " ms"); //print total reading time
        return lines;
    }

    /**
     * reads number of total lines in given files, using a thread pool
     * @param fileNames files to read
     * @return combined line count in all files
     */
    public static int getNumOfLinesThreadPool(String[] fileNames) {
        int n = fileNames.length;
        int result = 0;
        //thread pool with size of [n = fileNames.length] as instructed in the pdf
        ExecutorService ex = Executors.newFixedThreadPool(n); 
        List<Future<Integer>> threads = new ArrayList<>(n); //Futures used to wait for end of calculations

        long start = System.currentTimeMillis(); //start counting reading time

        //create callables [LineCounterCallable] to submit to thread and assign each one to a Future
        for (int i = 0; i < n; i++) {
            LineCounterCallable callable = new LineCounterCallable(fileNames[i]);
            threads.add(ex.submit(callable));
        }

        //wait for all Futures to read data
        for (int i = 0; i < n; i++) {
            try {
                result += threads.get(i).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();//stop counting reading time

        System.out.println("time = " + (end - start) + " ms"); //print total reading time
        
        //close running proccesses 
        ex.shutdown();
        threads.clear();
        return result;
    }

    /**
     * reads number of total lines in given files, using a thread pool
     * @param fileNames files to read
     * @param maxthreads max number of running threads
     * @return combined line count in all files
     */
    public static int getNumOfLinesThreadPool(String[] fileNames, int maxthreads) {
        int n = fileNames.length;
        int result = 0;
        //thread pool with size of [maxthreads]
        ExecutorService ex = Executors.newFixedThreadPool(maxthreads); 
        List<Future<Integer>> threads = new ArrayList<>(n); //Futures used to wait for end of calculations

        long start = System.currentTimeMillis(); //start counting reading time

        //create callables [LineCounterCallable] to submit to thread and assign each one to a Future
        for (int i = 0; i < n; i++) {
            LineCounterCallable callable = new LineCounterCallable(fileNames[i]);
            threads.add(ex.submit(callable));
        }

        //wait for all Futures to read data
        for (int i = 0; i < n; i++) {
            try {
                result += threads.get(i).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();//stop counting reading time

        System.out.println("time = " + (end - start) + " ms"); //print total reading time
        
        //close running proccesses 
        ex.shutdown();
        threads.clear();
        return result;
    }

    /**
     * delete files at [filepaths]
     * @param filepaths files to delete
     */
    public static void deleteFiles(String[] filepaths) {
        for(String filepath : filepaths) {
            File file = new File(filepath);
            file.delete();
        }
    }
}