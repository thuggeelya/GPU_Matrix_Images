package org.example;

import org.image.AbstractImageAnalyzer;
import org.image.ImageAnalyzer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.System.currentTimeMillis;

public class Application {

    public static void main(String[] args) throws IOException {
        int nQuantumLevels = Math.min(Runtime.getRuntime().availableProcessors(), 10);
        int nIterations = 10;
        List<Long> multipleThreadTimeList = new ArrayList<>();
        List<Long> singleThreadTimeList = new ArrayList<>();

        for (int iter = 0; iter < nIterations; iter++) {
            System.out.println("Iteration #" + iter);

            for (int n = 0, argsLength = args.length; n < argsLength; n++) {
                File file = new File(args[n]);
                BufferedImage img = ImageIO.read(file);
                int xWidth = img.getWidth();
                int yHeight = img.getHeight();
                int step = xWidth / nQuantumLevels;
                ExecutorService executor = Executors.newFixedThreadPool(nQuantumLevels);
                List<Future<?>> futures = new ArrayList<>();
                AbstractImageAnalyzer.setImage(new BufferedImage(xWidth, yHeight, BufferedImage.TYPE_INT_RGB));
                long start = currentTimeMillis();

                for (int i = 0; i < nQuantumLevels; i++) {
                    int remains = (i == nQuantumLevels - 1) ? xWidth % nQuantumLevels : 0;
                    futures.add(executor.submit(new ImageAnalyzer(i * step, (i + 1) * step + remains, img, nQuantumLevels)));
                }

                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println(e.getMessage());
                    }
                }

                ImageIO.write(AbstractImageAnalyzer.getImage(), "jpg",
                        new File("src/main/resources/out" + n + "_M" + ".jpg"));
                long timeSpent = currentTimeMillis() - start;
                multipleThreadTimeList.add(timeSpent);
                System.out.println(nQuantumLevels + " threads time, ms: " + timeSpent);

                try {
                    start = currentTimeMillis();
                    executor.submit(new ImageAnalyzer(0, xWidth, img, nQuantumLevels)).get();
                    ImageIO.write(AbstractImageAnalyzer.getImage(), "jpg",
                            new File("src/main/resources/out" + n + "_S" + ".jpg"));
                    timeSpent = currentTimeMillis() - start;
                    singleThreadTimeList.add(timeSpent);
                    System.out.println("1 thread  time, ms: " + timeSpent);
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println(e.getMessage());
                } finally {
                    executor.shutdown();
                }
            }
        }

        System.out.println();
        System.out.println(nQuantumLevels + " threads average time, ms: " +
                multipleThreadTimeList.stream().mapToLong(Long::longValue).average().orElse(0d));
        System.out.println("1 thread  average time, ms: " +
                singleThreadTimeList.stream().mapToLong(Long::longValue).average().orElse(0d));
    }
}
