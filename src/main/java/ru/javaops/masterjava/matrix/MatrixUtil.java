package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {


    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final List<Future<?>> futureList = new ArrayList<>();
        final int[][] matrixT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {

            for (int j = 0; j < matrixSize; j++) {
                matrixT[j][i] = matrixB[i][j];
            }
        }
        for (int i = 0; i < matrixSize; i++) {

            final int m = i;
            futureList.add(executor.submit(() -> {

                for (int j = 0; j < matrixSize; j++) {
                    int[] rowA = matrixA[m];
                    int s = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        s += rowA[k] * matrixT[j][k];
                    }
                    matrixC[m][j] = s;
                }
            }));
        }

        for (Future future : futureList) {
            future.get();
        }
        return matrixC;
    }


    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[] columnB = new int[matrixSize];
        try {
            for (int i = 0; ; i++) {

                for (int j = 0; j < matrixSize; j++) {
                    columnB[j] = matrixB[j][i];
                }

                for (int j = 0; j < matrixSize; j++) {
                    int sum = 0;
                    int[] rowA = matrixA[j];

                    for (int k = 0; k < matrixSize; k++) {
                        sum += rowA[k] * columnB[k];
                    }
                    matrixC[j][i] = sum;
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }

        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
