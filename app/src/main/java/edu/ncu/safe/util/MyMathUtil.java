package edu.ncu.safe.util;

/**
 * Created by Mr_Yang on 2016/5/26.
 */
public class MyMathUtil {

    /**
     * 一个圆中中的一个扇形去掉以半径为边后的小弧面积咱整个圆的百分比（percent）
     * 根据percent求圆心角
     * @param percent
     * @return
     */
    public static float toAngle(float min,float max,float percent,int times){
        if(percent<0.001){
            return 0;
        }
        float mid = (min + max) / 2;
        if(times==0) {
            return mid;
        }
        double m = (mid/360.0-Math.sin(mid*Math.PI/180.0)/(2*Math.PI))*100;

        if(m>percent){
            return toAngle(min,mid,percent,times-1);
        }else{
            return toAngle(mid,max,percent,times-1);
        }
    }
}
