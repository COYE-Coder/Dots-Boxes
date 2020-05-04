package com.DotsAndBoxes;

import javafx.geometry.Point2D;
import java.util.ArrayList;



public class pt {
    private ArrayList<uLine> lines = new ArrayList<uLine>();
    private Point2D coord;
    private int noDrawn = 0;


    public pt(Point2D coord) {
        this.coord = coord;
    };

    public int getNoDrawn() {
        return noDrawn;
    }

    public ArrayList<uLine> getLines() {
        return lines;
    }

    public void setLines(ArrayList<uLine> lines) {
        this.lines = lines;
    }

    public Point2D getCoord() {
        return coord;
    }

    public void setCoord(Point2D coord) {
        this.coord = coord;
    }

    public boolean inArea(int x, int y) {
        if ((x < 0) || (y < 0)){
            return false;
        }
        else if ((x > 9) || (y>9)) {
            return false;
        }
    return true;
    }




    public void populateNeighbors(int x, int y){
        //Given an x,y coordinate, return all lines such that:
        // . | .
        // .-.-.
        // . | .
        Point2D center = new Point2D(x*50,y*50);



        //First populate an arrayList of first-order neighborhood:
        // Returns coordinates in this order:
        // . 4 .
        // 1 . 2
        // . 3 .

        ArrayList<Point2D>  neighbors = new ArrayList<Point2D>();
        for (int i = x - 1; i < x + 2; i += 2){
            if (inArea(i,y)){
                neighbors.add(new Point2D(i*50,y*50));

            }
        }

        for (int j = y - 1; j < y + 2; j += 2){
            if (inArea(x,j)) {
                neighbors.add(new Point2D(x*50, j*50));
            }
        }


        //then populate this.lines with each uLine object
        for(Point2D element : neighbors){
            Point2D[] pointList = new Point2D[]{center,element};
            uLine l = new uLine(pointList);
            this.lines.add(l);
        }
    }

    public void incrementDrawn(){
        this.noDrawn++;
    }



}
