import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FilterOutputStream;
import java.util.concurrent.*;


public class Ex2_1 {
    /**
     * ////     * @param n     - represent the file names
     * ////     * @param seed  - Creates a new random number generator using a single long seed.
     * ////     * @param bound - "" "" ""
     * //
     */
//    FileInputStream fileInputStream = FileInputStream
//    BufferedWriter bw = new BufferedWriter
    public static String[] createTextFiles(int n, int seed, int bound) {
        if (n <= 0 || seed < 0 || bound < 1) {
            throw new IllegalArgumentException("invalid input parameters");
        }
        String[] fileNames = new String[n];
        Random rand = new Random(seed);
        for (int i = 0; i < n; i++) {
            String fileName = "file_" + (i + 1) + ".txt";
            fileNames[i] = fileName;
            int lineNumber = rand.nextInt(bound) + 1;
            File file = new File(fileName);
            int fileIndex=1;
            while(file.exists()){
                file = new File(fileName+"_"+fileIndex);
                fileIndex++;
            }
            try {
                FileOutputStream fw = new FileOutputStream(file);
                for (int j = 0; j < lineNumber; j++) {
                    //System.lineSeparator() is insert a new line in a text file
                    String line = "hellow world" + System.lineSeparator();
                        fw.write(line.getBytes());
                }
                fw.close();
            } catch (IOException e) {
                System.out.println("Error creating file: " + fileName);
            }
        }
        return fileNames;
    }
        public static void deleteFile(String fileNames) {
            File file = new File(fileNames);
            if (file.delete()) {
                System.out.println(file.getName() + " has been deleted.");
            } else {
                System.out.println("Failed to delete " + file.getName());
            }
        }
    /**
     * @param fileNames array that contains file names
     * @return the total number of lines of the files
     */
    public static int getNumOfLines(String[] fileNames) {
        int countLines = 0;
        for (String fileName : fileNames) {
            File myfile = new File(fileName);
            try {
                BufferedReader read_frome = new BufferedReader(new FileReader(myfile));
                while (read_frome.readLine() != null) {
                    countLines++;
                }
                read_frome.close();
            } catch (IOException e) {
                System.out.println("Error reading file: " + fileName);
            }
        }
        return countLines;
    }

    public static class LineCounterThread extends Thread {//האם אפשר לעשות אותו סטטי? כי זה מחלקה שאני משתמשת בה
        private String fileName;
        private int countLines;

        public LineCounterThread(String fileName) {
            this.fileName = fileName;
            this.countLines = 0;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                while (reader.readLine() != null) {
                    countLines++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public int getCountLines() {
            return countLines;
        }
    }


    /**
     * //     *
     * //     * @param fileNames array that contains file names
     * //     * @return the total number of lines of the files
     * //
     */
    public int getNumOfLinesThreads(String[] fileNames) {
        int lines = 0;
        //creats an arry that heritat from lineconter thread class
        LineCounterThread[] threads = new LineCounterThread[fileNames.length];
        try {
            for (int i = 0; i < fileNames.length; i++) {
                //creating a new program for every file
                threads[i] = new LineCounterThread(fileNames[i]);
                // starting the program
                threads[i].start();
            }
            //waiting for al of the programs to be done by the function join
            for (int i = 0; i < threads.length; i++) {
                if (!threads[i].isAlive()) {//לנסות משהו אחר
                    lines += threads[i].getCountLines();
                } else {
                    threads[i].join();
                    lines += threads[i].getCountLines();
                }
            }
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        return lines;
    }


    public static class LineCounterThreadpool implements Callable<Integer> {
        private String fileName;
        private int countLines;
        public LineCounterThreadpool(String fileName){
            this.fileName=fileName;
            this.countLines=0;
        }
        @Override
        public Integer call() throws Exception {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                while (reader.readLine() != null) {
                    countLines++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return countLines;
        }
        public int getLineCounterThreadpool() {
            return countLines;
        }
    }
    /**
     * @param fileNames array that contains file names
     * @return the total number of lines of the files
     * //
     */
    public static int getNumOfLinesThreadPool(String[] fileNames) {
        int n = fileNames.length;
        int result=0;
        ExecutorService ex = Executors.newFixedThreadPool(n);
        Future<Integer> thread[] = new Future[n];
        //creating a new program for every file
        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            LineCounterThreadpool threadpool = new LineCounterThreadpool(fileNames[i]);
            thread[i] = ex.submit(threadpool);
           // System.out.println("Thread " + i + " returned value: " + results[i - 1].get());
        }
        for (int i = 0; i < n; i++) {
            try {
                result += thread[i].get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - start) + " ms");
        ex.shutdown();
        return result;
    }

    public static void main(String[] args) {
        int n = 2, seed = 1, bound = 10;
        // File f = new File("file_" + n);
        String[] fileNames = createTextFiles(n, seed, bound);
        getNumOfLinesThreadPool(fileNames);
    }
}



