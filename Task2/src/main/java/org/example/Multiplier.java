package org.example;

import java.util.concurrent.Callable;

public class Multiplier implements Callable<Float[][]> {

    private final int from;
    private final int to;
    private final int size;
    private final float[][] A;
    private final float[][] B;
    private final Float[][] result;

    public Multiplier(int from, int to, float[][] a, float[][] b) {
        this.from = from;
        this.to = to;
        size = a.length;
        A = a;
        B = b;
        result = new Float[to - from][size];
    }

    public Multiplier(float[][] a, float[][] b) {
        from = 0;
        size = a.length;
        to = size;
        A = a;
        B = b;
        result = new Float[to - from][size];
    }

    @Override
    public Float[][] call() {
        for (int i = from; i < to; ++i) {
            for (int j = 0; j < size; ++j) {
                int iResult = i - from;
                result[iResult][j] = 0f;

                for (int j2 = 0; j2 < size; ++j2) {
                    result[iResult][j] += A[i][j2] * B[j2][j];
                }
            }
        }

        return result;
    }
}
