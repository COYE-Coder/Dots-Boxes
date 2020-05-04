package com.DotsAndBoxes;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.geometry.*;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.geometry.Point2D;
import javafx.css.PseudoClass;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Paint;



public class Main extends Application{
    public static int turn = 1;
    public static int xDim = 500;
    public static int yDim = 500;
    public static int numCells = 1;
    public static Point2D beginPoint;
    public static Point2D endPoint;
    public boolean beingDrawn = true;
    public pt[][] grid = new pt[10][10];
    public static int noCircles = 0;
    public static int mSq =0;



    public static void main(String[] args) {
        launch(args);
    }

    private AnchorPane root;
    private Line l;

    public void start(Stage primaryStage) {



        root = new AnchorPane();

        Pane overlay = new Pane();

        //Draw Circles via pixel values (50, 100, 150, etc)
        for (int i = 50; i < xDim; i += 50) {
            for (int j = 50; j < yDim; j += 50) {
                createFrontCircle(i,j, overlay);
            }
        }

        //Initialize grid for backend logic
        for (int i = 0; i < grid.length; i ++){
            for (int j = 0; j < grid[i].length; j++){
                int frontX = i * 50;
                int frontY = j * 50;
                //Try changing coord to be on grid scale:
                Point2D coord = new Point2D(i,j);
                //int[] coord = new int[]{frontX,frontY};
                pt p = new pt(coord);
                p.populateNeighbors(i,j);
                grid[i][j] = p;
            }
        }



        root.getChildren().addAll(overlay);
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();


        //Troubleshooting:
//        pt t = grid[1][1];
//        pt t2 = grid[2][1];
//        ArrayList<pt> tNeigh = secondOrder(t,grid);
//        ArrayList<pt> tNeigh2 = secondOrder(t2,grid);

//        uLine tl1 = t.getLines().get(1);
//        uLine tl2 = t2.getLines().get(0);
//        System.out.println(tl1.getEnds()[0] + "," + tl1.getEnds()[1]);
//        System.out.println(tl2.getEnds()[0] + "," + tl2.getEnds()[1]);
//        System.out.println(lineEqual(tl1,tl2));


//        ArrayList<pt> inter = interPoints(tNeigh,tNeigh2);
//        for (pt p : inter){
//            System.out.println("Testing inter Points: " + p.getCoord());
//        }



        //System.out.println(inter.size());


    }

    private void createFrontCircle(int x, int y, Pane overlay) {
        Circle circle = new Circle();
        //circle.getStyleClass().add("interLines");
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(5);
        overlay.getChildren().add(circle);

        circle.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            Point2D tempPoint = new Point2D(x, y);
            //If this circle is the first one:
            if (beingDrawn) {
                beginPoint = tempPoint;
            } else{
                endPoint = tempPoint;
                //Then draw a line between beginPoint and endPoint:
                Line l = drawLine(beginPoint,endPoint);
                overlay.getChildren().add(l);
            }

            //Then reverse beingDrawn
            beingDrawn = !beingDrawn;

            //Draw Circles again to prevent lines overwriting circles
            for (int i = 50; i < xDim; i += 50) {
                //populate coords[x]
                for (int j = 50; j < yDim; j += 50) {
                    createFrontCircle(i,j, overlay);
                }
            }

        });

        noCircles++;
    }

    private void markDrawn(Point2D p1, Point2D p2){
        uLine l = new uLine(new Point2D[]{p1,p2});

        //Search grid[][] for every pt which has a uLine with
        //coords p1 and p2, marking it asDrawn
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[i].length; j++){
                pt tempP = grid[i][j];

                //Iterate over every uLine object within each pt in grid
                for (uLine element:tempP.getLines()){

                    //If the uLine element has EndPoints like the point in drawLine
                    if (lineEqual(l,element)){
                        element.setDrawn(true);
                        tempP.incrementDrawn();

                        System.out.println("Made Square? : " + madeSquare(tempP));

                    }
                }
            }
        }
    }

    public Line drawLine(Point2D p1, Point2D p2){
        Line l = new Line();
        double circle1X = p1.getX();
        double circle1Y = p1.getY();
        l.setStartX(circle1X);
        l.setStartY(circle1Y);


        double circle2X = p2.getX();
        double circle2Y = p2.getY();
        l.setEndX(circle2X);
        l.setEndY(circle2Y);

        l.setStrokeWidth(3.0f);

        //Set different colors based on turn
        turn += 1;
        if (turn % 2 !=0){
            l.setStroke(Color.BLUE);
        }
        else{l.setStroke(Color.RED);}


        //Mark the line as drawn
        markDrawn(p1,p2);


        System.out.println("===========");
//        System.out.println("number of circles drawn: " + noCircles);
//        System.out.println("Number of neighborhoods considered: " + noNeighborhoods);


        return l;
    };

    public boolean inArea(int x, int y) {
        if ((x < 0) || (y < 0)){
            return false;
        }
        else if ((x > 9) || (y > 9)) {
            return false;
        }
        return true;
    }


    //Returns second order neighborhood structure around a given point
    private ArrayList<pt> secondOrder(pt p, pt[][] grid){
        int x  = (int) p.getCoord().getX();
        int y = (int) p.getCoord().getY();
        int t3 = 0;
        //System.out.println("Coordinates in pt: " + x + ',' + y);
        ArrayList<pt> neighborhood = new ArrayList<pt>();


        for (int i = x-1; i < x+2; i++){
            for (int j = y-1; j< y+2; j++){
                if (inArea(i,j)){
                    if (i == x && j ==y){
                        continue;

                    }else{
                        pt n = grid[i][j];
                        neighborhood.add(n);
                    }

                }
            }
        }

        //System.out.println("end of secondOrder");
        return neighborhood;
    }

    //Find intersecting points of two different neighborhoods lists

    private ArrayList<pt> interPoints(ArrayList<pt> n1, ArrayList<pt> n2){
        //Iterate over each "neighborhood"
        ArrayList<pt> interPoint = new ArrayList<pt>();
        ArrayList<pt> interUnique = new ArrayList<pt>();

        for (pt point1:n1) {
            for (pt point2 : n2) {
                if (point1.getCoord().equals(point2.getCoord())){
                    interPoint.add(point1);
//                    System.out.println("Do these equal? " + point1.getCoord() + "\n      vs " + point2.getCoord());

                }
            }
        }

        //Remove uniques
        for(pt p1:interPoint){
            if(!interUnique.contains(p1)){
                interUnique.add(p1);
            }
        }
        return interUnique;
    }

    //Then find all lines that are the same
    private ArrayList<uLine> interLines(ArrayList<pt> n1,ArrayList<pt> n2) {
        ArrayList<uLine> interLines = new ArrayList<uLine>();
        ArrayList<pt> interPoints = interPoints(n1, n2);

//        System.out.println("Interpoints size: " + interPoints.size());


        //Only check between common points.

        //Iterate over the center square:
        for (int i = 0; i < interPoints.size(); i++) {
            for (int j = 0; j < interPoints.size(); j++) {
                pt point1 = interPoints.get(i);
                pt point2 = interPoints.get(j);

                if ((point1.getNoDrawn() > 1) &&
                        (point2.getNoDrawn() > 1)){
                //For each line in the point intersection
                    for (uLine line1 : point1.getLines()) {
                        for (uLine line2 : point2.getLines()) {


                            //If two lines are drawn
                            if (line1.isDrawn && line2.isDrawn) {


                                //Check to see if they are the same lines
                                if (!lineEqual(line1, line2)) {
                                    interLines.add(line1);
                                    interLines.add(line2);
    //                                System.out.println(line1.getEnds()[0]);
    //                                System.out.println(line1.getEnds()[1]);
                                }

                            }
                        }
                    }
                }
            }
        }

        ArrayList<uLine> noDupe = new ArrayList<>();
        //Remove duplicates:
        for (uLine element:interLines){
            if (!noDupe.contains(element)){
                noDupe.add(element);
            }
        }



        //Troubleshooting

//        Print all noDupe Lines:
//        for (uLine element:noDupe){
//            for(Point2D p1:element.getEnds()){
//                System.out.println("Points: " + p1 + "------");
//            }
//            System.out.println("==========\nNew Line: ");
//        }
//
//        System.out.println("New InterLines ****************");
//
//        System.out.println("noDupe size: " + noDupe.size());

    return noDupe;

    }


    //Line is equal:
    private boolean lineEqual(uLine l1, uLine l2){
        //Since each line has two end points (stored in a Point2D[]),
        //Two lines can be declared unequal even though (x,y) == (y,x)
        ArrayList<Point2D> allPoints = new ArrayList<>();
        ArrayList<Point2D> allUniques = new ArrayList<>();

        for (Point2D p1:l1.getEnds()){
            for (Point2D p2:l2.getEnds()){
                allPoints.add(p1);
                allPoints.add(p2);
            }
        }

        for(Point2D p3:allPoints){
            if (!allUniques.contains(p3)){
                allUniques.add(p3);
            }
        }

        if (allUniques.size() == 2){
            return true;
        }

        //System.out.println("Uniques size: " + allUniques.size());


        return false;
    }

    //Start square logic

    private boolean madeSquare(pt p){

        ArrayList<uLine> pLines = p.getLines();
        ArrayList<pt> neighborhood = secondOrder(p,grid);
        ArrayList<pt> pointDrawn = findDrawn(p);
        int pNoDrawn = p.getNoDrawn();

        //System.out.println("tempP getNoDrawn: " + p.getNoDrawn());

        //If point doesn't have at least two drawn, it cannot make a square.
        if (pNoDrawn ==1 ){
            return false;
        }

        //If point has two at least 2 drawn, we check each of those two neighborhoods
        else if (pNoDrawn > 1) {

            //For every point that is at the end of a drawn line:
            for (pt element : pointDrawn) {

                ArrayList<uLine> inter = new ArrayList<uLine>();
                int neighDrawn = element.getNoDrawn();
                //If the endPoint has only 1 line drawn, it cannot have made a square
                //If a neighborhood point has at least two lines drawn:
                if (neighDrawn > 1) {
                    //One has to be the origin.
                    inter = interLines(secondOrder(element, grid),
                            neighborhood);
                    if (inter.size() >= 4) {
                        return true;
                    }
                }

                //If neighborhood point has 2 lines drawn, it must be this configuration
                // (Right angle):
            /*
            . . . . . .
            . . . . . .
            . . . * x *
            . . . . . x
            . . . . . X
             */

            }
        }

        mSq ++;
        return false;
    }


    //Given any 1 point that is connected to at least 2 drawn lines:
    //return the points that are at the end of the line, neglecting the origin
    public ArrayList<pt> findDrawn(pt p){
        ArrayList<pt> allDrawn = new ArrayList<pt>();
        ArrayList<Point2D> pointsDrawn = new ArrayList<Point2D>();
        ArrayList<uLine> linesDrawn = new ArrayList<uLine>();

        for (uLine l1:p.getLines()){
            if (l1.isDrawn){
                linesDrawn.add(l1);
            }
        }



        for (uLine l2:linesDrawn){
            for(Point2D p1:l2.getEnds()){
                if ((!pointsDrawn.contains(p1)) &&
                        (!Point2DEquals(p1,p.getCoord()))){
                    pointsDrawn.add(p1);
                }
            }
        }

        //Find pt at the Point2D coordinates:
        for (Point2D p2:pointsDrawn){
            int x = (int) p2.getX();
            int y = (int) p2.getY();
            int ptX = (x-50)/50;
            int ptY = (y-50)/50;
            pt p3 = grid[ptX][ptY];
            allDrawn.add(p3);
        }





        return allDrawn;
    }


    public boolean Point2DEquals(Point2D p1,Point2D p2){
        if (p1.equals(p2)){
            return true;
        }
        else if((p1.getX() == p2.getY()) ||
                p2.getX() == p1.getY()){
            return true;
        }
        return false;
    }
}
