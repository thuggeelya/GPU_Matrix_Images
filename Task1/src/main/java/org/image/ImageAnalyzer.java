package org.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageAnalyzer extends AbstractImageAnalyzer implements Runnable {

    private static double Q_WIDTH = 0.0d;
    private final int x1;
    private final int x2;
    private final BufferedImage img;

    public ImageAnalyzer(int x1, int x2, BufferedImage img, int nQuantumLevels) {
        this.x1 = x1;
        this.x2 = x2;
        this.img = img;

        if (Q_WIDTH == 0.0d) {
            Q_WIDTH = 255d / nQuantumLevels;
        }
    }

    @Override
    public void run() {
        for (int x = x1; x < x2; x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixel = img.getRGB(x, y);
                Color color = new Color(pixel, false);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
//                int r = (pixel >> 16) & 0xff;
//                int g = (pixel >> 8) & 0xff;
//                int b = pixel & 0xff;
                double i = (r + g + b) / 3d;
                int newRGB = QUANTUMS[(int) (i / Q_WIDTH)];
                image.get().setRGB(x, y, newRGB);
            }
        }
    }
}
