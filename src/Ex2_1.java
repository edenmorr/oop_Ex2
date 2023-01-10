import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FilterOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
            String fileName = "file_" + (i + 1);
            fileNames[i] = fileName;
            File file_ = new File(fileName);
            boolean b = file_.mkdir();
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file_));
//                FileWriter fWrite = new FileWriter(file_);// i change to BufferedWriter just in case that the text will be long
                int lineNumber = rand.nextInt(bound) + 1;
                for (int j = 0; j < lineNumber; j++) {
                    while (file_.exists()) {
                        bufferedWriter.write("hellow world\n");
                    }
                }
                bufferedWriter.close();
            } catch (IOException e) {
                System.out.println("Error creating file: " + fileName);
            }
        }
        return fileNames;
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
        for (int i = 0; i < fileNames.length; i++) {
            //creating a new program for every file
            threads[i] = new LineCounterThread(fileNames[i]);
            // starting the program
            threads[i].start();
        }
        //waiting for al of the programs to be done by the function join
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // the total amount of the lines of all of the files
            lines += threads[i].getCountLines();
        }
        return lines;
    }

    /**
     * @param fileNames array that contains file names
     * @return the total number of lines of the files
     * //
     */
    public class LineCounterThreadpool implements Callable {
        private String fileName;
        private int countLines;
        public LineCounterThreadpool(String fileName){
            this.fileName=fileName;
            this.countLines=0;
        }
        @Override
        public Object call() throws Exception {
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
    public int getNumOfLinesThreadPool(String[] fileNames) {
        ExecutorService ex = Executors.newFixedThreadPool(fileNames.length);
        for (int i = 0; i < fileNames.length; i++) {
            //creating a new program for every file
//            ex.submit(new LineCounterThreadpool(i))=
            // starting the program
            ex.submit(new LineCounterThreadpool("file_"+i+1));

        }
        //waiting for al of the programs to be done by the function join
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // the total amount of the lines of all of the files
            lines += threads[i].getCountLines();
        }
        return lines;
    }        return 0;
    }

    public static void main(String[] args) {
        int n = 9, seed = 1, bound = 100;
        File f = new File("file_" + n);
        createTextFiles(n, seed, bound);
        String[] fileNames = {"file1.txt", "file2.txt", "file3.txt"};
    }
}



