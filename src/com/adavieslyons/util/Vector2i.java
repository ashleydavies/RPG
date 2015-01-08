package com.adavieslyons.util;

import org.newdawn.slick.geom.Vector2f;

public class Vector2i {
    private int x;
    private int y;

    public Vector2i() {
        this(0, 0);
    }

    public Vector2i(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Vector2f lerpTo(Vector2i other, double fraction) {
        return new Vector2f((float) (getX() + (other.getX() - getX())
                * fraction), (float) (getY() + (other.getY() - getY())
                * fraction));
    }

    @Override
    public String toString() {
        return "x: " + Integer.toString(x) + " y: " + Integer.toString(y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(getClass()))
            return false;

        Vector2i other = (Vector2i) obj;
        return (other.getX() == getX() && other.getY() == getY());
    }

    public Vector2i add(Vector2i other) {
        return new Vector2i(getX() + other.getX(), getY() + other.getY());
    }

    public Vector2i subtract(Vector2i other) {
        return new Vector2i(getX() - other.getX(), getY() - other.getY());
    }

    public int distance(Vector2i other) {
        return (int) (Math.sqrt(
                Math.pow(getX() - other.getX(), 2.0)
                        + Math.pow(getY() - other.getY(), 2.0)
        ));
    }
}
