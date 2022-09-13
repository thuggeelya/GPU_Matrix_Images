package org.example;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

        int nThreads = Runtime.getRuntime().availableProcessors();
        int nIterations = 10;
        List<Long> multipleThreadTimeList = new ArrayList<>();
        List<Long> singleThreadTimeList = new ArrayList<>();
        List<Future<Map.Entry<Integer, Float[][]>>> futures = new ArrayList<>(nThreads);

        for (int i = 0; i < nIterations; i++) {
            System.out.println("Iteration #" + i);
            futures.clear();
            ExecutorService executor = Executors.newFixedThreadPool(nThreads);
            int step = SIZE / nThreads;
            long start = currentTimeMillis();

            for (int j = 0; j < nThreads; j++) {
                int remains = (j == nThreads - 1) ? SIZE % nThreads : 0;
                futures.add(executor.submit(new Multiplier(j * step, (j + 1) * step + remains, A, B, j)));
            }

            Map<Integer, Float[][]> resultMap = new TreeMap<>();

            for (Future<Map.Entry<Integer, Float[][]>> future : futures) {
                try {
                    Map.Entry<Integer, Float[][]> entry = future.get();
                    resultMap.put(entry.getKey(), entry.getValue());
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println(e.getMessage());
                }
            }

            Float[][] resultMultipleThread = new Float[SIZE][SIZE];
            int next = 0;

            for (Map.Entry<Integer, Float[][]> entry : resultMap.entrySet()) {
                Float[][] val = entry.getValue();
                int entryLength = val.length;

                for (int row = 0; row < entryLength; row++) {
                    System.arraycopy(val[row], 0, resultMultipleThread[row + next], 0, SIZE);
                }

                next += entryLength;
            }

            long timeSpent = currentTimeMillis() - start;
            multipleThreadTimeList.add(timeSpent);
            System.out.println(nThreads + " threads time, ms: " + timeSpent);

            try {
                start = currentTimeMillis();
                Float[][] resultSingleThread = executor.submit(new Multiplier(A, B)).get().getValue();
                timeSpent = currentTimeMillis() - start;

                for (int r1 = 0; r1 < SIZE; ++r1) {
                    for (int r2 = 0; r2 < SIZE; ++r2) {
                        if (!Objects.equals(resultMultipleThread[r1][r2], resultSingleThread[r1][r2])) {
                            printMatrix(resultMultipleThread);
                            System.out.println();
                            printMatrix(resultSingleThread);
                            throw new RuntimeException("Error: results differ ..");
                        }
                    }
                }

                singleThreadTimeList.add(timeSpent);
                System.out.println("1 thread  time, ms: " + timeSpent);
            } catch (InterruptedException | ExecutionException e) {
                System.err.println(e.getMessage());
            } finally {
                executor.shutdown();
            }
        }

        System.out.println();
        System.out.println(nThreads + " threads average time, ms: " +
                multipleThreadTimeList.stream().mapToLong(Long::longValue).average().orElse(0d));
        System.out.println("1 thread  average time, ms: " +
                singleThreadTimeList.stream().mapToLong(Long::longValue).average().orElse(0d));
    }

    private static void printMatrix(Float[][] matrix) {
        for (Float[] floats : matrix) {
            for (Float aFloat : floats) {
                System.out.printf("%.1f  ", aFloat);
            }

            System.out.println();
        }
    }
}
