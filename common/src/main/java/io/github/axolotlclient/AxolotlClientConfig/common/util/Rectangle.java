package io.github.axolotlclient.AxolotlClientConfig.common.util;

/*
 * Stores a basic rectangle.
 */


public class Rectangle {

    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

    public boolean isMouseOver(double mouseX, double mouseY){
        return mouseX>=x && mouseX<=x+width && mouseY >=y && mouseY <= y+height;
    }

    public Rectangle setData(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }
}
