package com.marven.fyp.memorytraining;

import java.io.Serializable;

/**
 * Created by Marven on 02-01-18.
 */

public class PointXY implements Serializable {

    private double x;
    private double y;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;


    public PointXY(double x, double y){
        this.x = x;
        this.y = y;
    }

    public PointXY(double minX, double maxX, double minY, double maxY){
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }



    public double x(){
        return x;
    }
    public double minX(){
        return minX;
    }
    public double maxX(){return maxX; }

    public double y(){
        return y;
    }
    public double minY(){
        return minY;
    }
    public double maxY(){return maxY; }


}
