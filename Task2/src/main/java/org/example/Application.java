package org.example;

import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class Application {

    private static final int SIZE = 4096;

    public static void main(String[] args) {
        Random random = new Random();
        float[][] A = new float[SIZE][SIZE];
        float[][] B = new float[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                A[i][j] = random.nextFloat();
                B[i][j] = random.nextFloat();
            }
        }

        int nIterations = 10;

        for (int i = 0; i < nIterations; i++) {
            System.out.println("Iteration #" + i);
            long start = currentTimeMillis();
            @SuppressWarnings("unused")
            Float[][] C = new Float[SIZE][SIZE];
            runGPU(A, B, C);
            long timeSpent = currentTimeMillis() - start;
            System.out.println("GPU time, ms: " + timeSpent);
        }
    }

    private static void runGPU(float[][] a, float[][] b, Float[][] c) {
        new TaskGraph("s0")
                .transferToDevice(DataTransferMode.FIRST_EXECUTION, a, b)
                .task("t0", Application::taskToExecute, a, b, c, SIZE)
                .transferToHost((Object) c)
                .execute();
    }

    public static void taskToExecute(float[][] a, float[][] b, Float[][] c, int size) {
        for (@Parallel int i = 0; i < size; ++i) {
            for (@Parallel int j = 0; j < size; ++j) {
                c[i][j] = 0f;

                for (int j2 = 0; j2 < size; ++j2) {
                    c[i][j] += a[i][j2] * b[j2][j];
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private static void printMatrix(Float[][] matrix) {
        for (Float[] floats : matrix) {
            for (Float aFloat : floats) {
                System.out.printf("%.1f  ", aFloat);
            }

            System.out.println();
        }
    }
}