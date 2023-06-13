package com.fisheep.geocalcu.util;

import com.fisheep.geocalcu.bean.GeoPoint;

public class GeofencingUtils {

    /**
     * 地球半径(米)
     */
    private static final double EARTH_RADIUS = 6378137.0;


    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 计算是否在圆内
     *
     * @param radius 半径（单位/米）
     * @param p1     圆心坐标
     * @param p2     判断点坐标
     * @return: boolean true:在圆内,false:在圆外
     * @date: 2021-11-08 09:44:54
     */
    public static boolean isInCircle(double radius, GeoPoint p1, GeoPoint p2) {
        double radLat1 = rad(p1.getLat());
        double radLat2 = rad(p2.getLat());
        double a = radLat1 - radLat2;
        double b = rad(p1.getLng()) - rad(p2.getLng());
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return !(s > radius);
    }
}