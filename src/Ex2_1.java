import java.util.Random;
import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FilterOutputStream;


public class Ex2_1 {

    /**
     ////     * @param n     - represent the file names
     ////     * @param seed  - Creates a new random number generator using a single long seed.
     ////     * @param bound - "" "" ""
     //     */
//    FileInputStream fileInputStream = FileInputStream
//    BufferedWriter bw = new BufferedWriter
    public static String[] createTextFiles(int n, int seed, int bound) {
        if (n <= 0 || seed < 0 || bound < 1) {
            throw new IllegalArgumentException("invalid input parameters");
        }
        String[] fileNames = new String[n];
        Random rand = new Random(seed);
        for (int i = 0; i <= n; i++) {
            String fileName  = "file_" + (i + 1);
            fileNames[i] = fileName;
            File file_ = new File(fileName);
            boolean b = file_.mkdir();
            try{
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file_));
//                FileWriter fWrite = new FileWriter(file_);// i change to BufferedWriter just in case that the text will be long
                    int lineNumber = rand.nextInt(bound) + 1;
                    for (int j = 0; j < lineNumber; j++) {
                        while( file_.exists()){
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
     *
     * @param fileNames array that contains file names
     * @return the total number of lines of the files
     */
    public static int getNumOfLines(String[] fileNames){
        int countLines = 0;
        for (String fileName:fileNames) {
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


    /**
     //     *
     //     * @param fileNames array that contains file names
     //     * @return the total number of lines of the files
     //     */
    public int getNumOfLinesThreads(String[] fileNames){
        int countLines = 0;

        return 0;
    }

    /**
     *
     * @param fileNames array that contains file names
     * @return the total number of lines of the files
    //     */
    public int getNumOfLinesThreadPool(String[] fileNames){
        return 0;
    }

    public static void main(String[] args) {
        int n=9, seed=1, bound=100;
        File f = new File("file_"+ n);
        createTextFiles(n,seed,bound);
    }

}



