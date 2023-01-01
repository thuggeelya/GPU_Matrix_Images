package org.example;

import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class Application {

    private static int SIZE = 4096;

    public static void main(String[] args) {
        if (args.length >= 1) {
            try {
                SIZE = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                SIZE = 4096;
            }
        }

        Random random = new Random();
        float[][] a = new float[SIZE][SIZE];
        float[][] b = new float[SIZE][SIZE];
        @SuppressWarnings("unused")
        float[][] c = new float[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                a[i][j] = random.nextFloat();
                b[i][j] = random.nextFloat();
            }
        }

        TaskGraph taskGraph = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.FIRST_EXECUTION, a, b)
                .task("t0", Application::taskToExecute, a, b, c, SIZE)
                .transferToHost((Object) c);

        long start = currentTimeMillis();
        taskGraph.execute();
        long timeSpent = currentTimeMillis() - start;
        System.out.println("GPU time, ms: " + timeSpent);
    }

    private static void taskToExecute(float[][] a, float[][] b, float[][] c, int size) {
        for (@Parallel int i = 0; i < size; ++i) {
            for (@Parallel int j = 0; j < size; ++j) {
                c[i][j] = 0.0f;
                float sum = 0.0f;

                for (int j2 = 0; j2 < size; ++j2) {
                    sum += a[i][j2] * b[j2][j];
                }

                c[i][j] = sum;
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