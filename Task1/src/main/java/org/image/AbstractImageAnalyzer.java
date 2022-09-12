package org.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractImageAnalyzer {

    protected static final AtomicReference<BufferedImage> image = new AtomicReference<>();
    protected static final int[] QUANTUMS = new int[]{
            new Color(0, 0, 0).getRGB(),
            new Color(127, 0, 0).getRGB(),
            new Color(255, 0, 0).getRGB(),
            new Color(0, 127, 0).getRGB(),
            new Color(0, 255, 0).getRGB(),
            new Color(0, 0, 127).getRGB(),
            new Color(0, 0, 255).getRGB(),
            new Color(127, 0, 127).getRGB(),
            new Color(127, 127, 0).getRGB(),
            new Color(0, 127, 127).getRGB()
    };

    public static BufferedImage getImage() {
        return image.get();
    }

    public static void setImage(BufferedImage img) {
        image.set(img);
    }
}
