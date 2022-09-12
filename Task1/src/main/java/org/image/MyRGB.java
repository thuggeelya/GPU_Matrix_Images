package org.image;

import java.awt.*;
import java.util.Objects;

public final class MyRGB {

    private final int red;
    private final int green;
    private final int blue;

    public MyRGB(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRGB() {
        return new Color(red, green, blue).getRGB();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyRGB myRGB = (MyRGB) o;
        return red == myRGB.red && green == myRGB.green && blue == myRGB.blue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue);
    }

    @Override
    public String toString() {
        return '(' + red + ", " + green + ", " + blue + ')';
    }
}
