package com.fisheep.geocalcu;

import com.fisheep.geocalcu.bean.GeoPoint;
import com.fisheep.geocalcu.bean.KinderGarten;
import com.fisheep.geocalcu.repo.KinderGartenRepo;
import com.fisheep.geocalcu.util.GeofencingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/calcu")
public class GeoCalcuApplication {

    private static BigDecimal latitude = new BigDecimal("38.897560");
    private static BigDecimal longituden = new BigDecimal("106.546840");
    private static List<KinderGarten> points;
    private static final BigDecimal fetch = new BigDecimal("0.000001");

    public static void main(String[] args) {
        SpringApplication.run(GeoCalcuApplication.class, args);
    }

    @Autowired
    private KinderGartenRepo kinderGartenRepo;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/mysql_all")
    public String mysqlCalcu() {
        points = kinderGartenRepo.findAll();
        return String.valueOf(points.size());
    }

    @RequestMapping("/mysql")
    public String mysql() {
        latitude = latitude.add(fetch);
        longituden = longituden.add(fetch);
        List<KinderGarten> all = kinderGartenRepo.findByDistance(longituden, latitude, Double.valueOf(30 * 1000L));
        return String.valueOf(all.size());
    }


    @RequestMapping("/memory")
    public String memory() {
        latitude = latitude.add(fetch);
        longituden = longituden.add(fetch);
        GeoPoint geoPointCurrent = new GeoPoint(Double.valueOf(longituden.toString()), Double.valueOf(latitude.toString()));

        List<KinderGarten> resPoints = new ArrayList<>();

        for (KinderGarten point : points) {
            GeoPoint geoPointTo = new GeoPoint(Double.valueOf(point.getLongitude().toString()), Double.valueOf(point.getLatitude().toString()));
            boolean inCircle = GeofencingUtils.isInCircle(30 * 1000L, geoPointCurrent, geoPointTo);
            if (inCircle) {
                resPoints.add(point);
            }
        }
        return String.valueOf(resPoints.size());
    }


    @RequestMapping("/redis")
    public String redis() {
        latitude = latitude.add(fetch);
        longituden = longituden.add(fetch);
        Point point1 = new Point(Double.valueOf(longituden.toString()), Double.valueOf(latitude.toString()));
        Distance distance1 = new Distance(30, Metrics.KILOMETERS);
        Circle circle = new Circle(point1, distance1);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius("geo:key", circle, args);
        return "{}";
    }
}
