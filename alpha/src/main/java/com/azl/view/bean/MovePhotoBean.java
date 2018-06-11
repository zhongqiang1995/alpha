package com.azl.view.bean;

/**
 * Created by zhong on 2017/11/28.
 */

public class MovePhotoBean {
    public MovePhotoBean(int x, int y, int width, int height, float radio) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.radio = radio;

    }

    float radio;
    int x;
    int y;
    int width;
    int height;

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public float getRadio() {
        return radio;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
