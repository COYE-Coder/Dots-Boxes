package com.DotsAndBoxes;
import javafx.geometry.Point2D;


public class uLine {
    public boolean isDrawn;
    private Point2D[] ends;

    public uLine(Point2D[] ends){
        this.ends = ends;
        this.isDrawn = false;
    }

    public uLine(int i, int y) {
    }

    public boolean isDrawn() {
        return isDrawn;
    }

    public void setDrawn(boolean drawn) {
        isDrawn = drawn;
    }

    public Point2D[] getEnds() {
        return ends;
    }

    public void setEnds(Point2D[] ends) {
        this.ends = ends;
    }
}
