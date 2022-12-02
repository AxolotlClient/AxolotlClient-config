package io.github.axolotlclient.AxolotlclientConfig.util;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * <p>License: GPL-3.0
 */

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
