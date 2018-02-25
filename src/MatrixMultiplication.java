import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {

    public static double[][] matrix1;
    public static double[][] matrix2;
    public static double[][] solution;
    private static Random random;
    public static int n;

    public static void main(String[] args) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("results.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int iterations = 20;
        //done for twenty sample size
        random = new Random(System.currentTimeMillis());
        for(int k=200; k<=2000; k+=200){
            n = k;
            matrix1 = new double[k][k];
            fillMatrix(matrix1);
            matrix2 = new double[k][k];
            fillMatrix(matrix2);
            solution = new double[k][k];
            long time = 0;
            for(int r=0; r<iterations; r++) {
                long startTime = System.currentTimeMillis();
                serialMultiplicator(matrix1, matrix2);
                long endTime = System.currentTimeMillis();
                time += endTime - startTime;
            }
            long average = time/iterations;
            try {
                writer.write(String.valueOf(average)+",");
            } catch (IOException e) {
                e.printStackTrace();
            }

            time = 0;
            for(int r=0; r<iterations; r++) {
                long startTime = System.currentTimeMillis();
                parallelMultiplicator(matrix1, matrix2);
                long endTime = System.currentTimeMillis();
                time += endTime - startTime;
            }
            average = time/iterations;
            try {
                writer.write(String.valueOf(average)+",");
            } catch (IOException e) {
                e.printStackTrace();
            }

            matrix2 = getTranspose(matrix2);
            time = 0;
            for(int r=0; r<iterations; r++) {
                long startTime = System.currentTimeMillis();
                transposedMultiplicator(matrix1, matrix2);
                long endTime = System.currentTimeMillis();
                time += endTime - startTime;
            }
            average = time/iterations;
            try {
                writer.write(String.valueOf(average));
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parallelMultiplicator(double[][] matrix1, double[][] matrix2){
        ExecutorService taskExecutor = Executors.newFixedThreadPool(n);//create a thread pool to run
        for(int i=0; i<n; i++) {
            final int x = i;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    for(int j=0; j<n; j++) {
                        solution[x][j] = 0;
                        for(int p=0; p<n; p++ ){
                            solution[x][j] += matrix1[x][p] * matrix2[p][j];
                        }
                    }
                }
            };
            taskExecutor.execute(r);
        }
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //serial version of matrix multiplication
    public static void serialMultiplicator(double[][] matrix1, double[][] matrix2){
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                solution[i][j] = 0;
                for(int p=0; p<n; p++ ){
                    solution[i][j] += matrix1[i][p] * matrix2[p][j];
                }
            }
        }
    }


    public static void transposedMultiplicator(double[][] matrix1, double[][] matrix2){
        ExecutorService taskExecutor = Executors.newFixedThreadPool(n);//create a thread pool to run
        for(int i=0; i<n; i++) {
            final int x = i;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    for(int j=0; j<n; j++) {
                        solution[x][j] = 0;
                        for(int p=0; p<n; p++ ){
                            solution[x][j] += matrix1[x][p] * matrix2[j][p];
                        }
                    }
                }
            };
            taskExecutor.execute(r);
        }
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //fill matrix with random numbers
    public static void fillMatrix(double[][] matrix){
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                matrix[i][j] = random.nextDouble();
            }
        }

    }

    //to get transpose of the matrix
    public static double[][] getTranspose(double[][] matrix) {
        double[][] transposedMatrix = new double[n][n];
        for(int i=0; i<n; ++i) {
            for (int j = 0; j < n; ++j) {
                transposedMatrix[j][i] = matrix[i][j];
            }
        }
        return transposedMatrix;
    }
}
