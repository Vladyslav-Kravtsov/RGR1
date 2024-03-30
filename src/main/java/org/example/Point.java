package org.example;

public class Point {
    private double y;
    private double dirY;
    public Point (double y, double dirY) {
        this.y = y;
        this.dirY = dirY;
    }

    public double getY() {
        return y;
    }

    public double getDirY() {
        return dirY;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDirY(double dirY) {
        this.dirY = dirY;
    }
}
