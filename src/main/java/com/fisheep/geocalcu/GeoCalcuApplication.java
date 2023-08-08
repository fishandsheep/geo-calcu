package com.fisheep.geocalcu;

import com.fisheep.geocalcu.bean.GeoPoint;
import com.fisheep.geocalcu.bean.KinderGarten;
import com.fisheep.geocalcu.repo.KinderGartenRepo;
import com.fisheep.geocalcu.util.GeofencingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/calcu")
public class GeoCalcuApplication implements CommandLineRunner {

    private static BigDecimal latitude = new BigDecimal("38.897560");
    private static BigDecimal longituden = new BigDecimal("106.546840");
    private static List<KinderGarten> points;
    private static final BigDecimal fetch = new BigDecimal("0.000001");
    private static StopWatch stopWatch = new StopWatch();
    private static int number = 0;


    public static void main(String[] args) {
        SpringApplication.run(GeoCalcuApplication.class, args);
    }

    @Autowired
    private KinderGartenRepo kinderGartenRepo;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/mysql_all")
    public String mysqlCalcu() {
        List<KinderGarten> all = kinderGartenRepo.findAll();
        return String.valueOf(all.size());
    }

    @RequestMapping("/mysql")
    public String mysql() {
        number = number >= 100 ? 0 : number;
        latitude = latitude.add(fetch);
        longituden = longituden.add(fetch);

        stopWatch.start();
        List<KinderGarten> all = kinderGartenRepo.findByDistance(longituden, latitude, Double.valueOf(30 * 1000L));
        stopWatch.stop();

        number++;
        if (number == 100) {
            StopWatch.TaskInfo[] taskInfo = stopWatch.getTaskInfo();
            StopWatch.TaskInfo max = Arrays.stream(taskInfo).max((o1, o2) -> Math.toIntExact(o1.getTimeMillis() - o2.getTimeMillis())).get();
            StopWatch.TaskInfo min = Arrays.stream(taskInfo).max((o1, o2) -> Math.toIntExact(o2.getTimeMillis() - o1.getTimeMillis())).get();
            int totalMillis = Arrays.stream(taskInfo).mapToInt(t -> Math.toIntExact(t.getTimeMillis())).reduce((x, y) -> x + y).getAsInt();

            System.out.println(max.getTimeMillis());
            System.out.println(min.getTimeMillis());
            System.out.println(totalMillis / 100);
        }
        return String.valueOf(all.size());
    }

    @Override
    public void run(String... args) {
//        StopWatch dataSync = new StopWatch("dataSync");
//        //查询数据库全部数据
//        dataSync.start();
//        points = kinderGartenRepo.findAll();
//        dataSync.stop();
//
//        dataSync.start();
//        List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>();
//        //导入至redis中
//        for (KinderGarten kinderGarten : points) {
//            Point point = new Point(Double.valueOf(kinderGarten.getLongitude().toString()),
//                    Double.valueOf(kinderGarten.getLatitude().toString()));
//            RedisGeoCommands.GeoLocation<String> location = new RedisGeoCommands.GeoLocation<>(String.valueOf(kinderGarten.getId()), point);
//            locations.add(location);
//            if (locations.size() == 500) {
//                redisTemplate.opsForGeo().add("geo:key", locations);
//                locations.clear();
//            }
//        }
//        if (locations.size() > 0) {
//            redisTemplate.opsForGeo().add("geo:key", locations);
//        }
//        dataSync.stop();
//        System.out.println(dataSync.prettyPrint());


    }


    @RequestMapping("/memory")
    public String memory() {
        number = number >= 100 ? 0 : number;
        latitude = latitude.add(fetch);
        longituden = longituden.add(fetch);
        GeoPoint geoPointCurrent = new GeoPoint(Double.valueOf(longituden.toString()), Double.valueOf(latitude.toString()));

        List<KinderGarten> resPoints = new ArrayList<>();

        stopWatch.start();
        for (KinderGarten point : points) {
            GeoPoint geoPointTo = new GeoPoint(Double.valueOf(point.getLongitude().toString()), Double.valueOf(point.getLatitude().toString()));
            boolean inCircle = GeofencingUtils.isInCircle(30 * 1000L, geoPointCurrent, geoPointTo);
            if (inCircle) {
                resPoints.add(point);
            }
        }
        stopWatch.stop();
        number++;
        if (number == 100) {
            StopWatch.TaskInfo[] taskInfo = stopWatch.getTaskInfo();
            StopWatch.TaskInfo max = Arrays.stream(taskInfo).max((o1, o2) -> Math.toIntExact(o1.getTimeMillis() - o2.getTimeMillis())).get();
            StopWatch.TaskInfo min = Arrays.stream(taskInfo).max((o1, o2) -> Math.toIntExact(o2.getTimeMillis() - o1.getTimeMillis())).get();
            int totalMillis = Arrays.stream(taskInfo).mapToInt(t -> Math.toIntExact(t.getTimeMillis())).reduce((x, y) -> x + y).getAsInt();

            System.out.println(max.getTimeMillis());
            System.out.println(min.getTimeMillis());
            System.out.println(totalMillis / 100);
        }

        return String.valueOf(resPoints.size());
    }


    @RequestMapping("/redis")
    public String redis() {
        number = number >= 100 ? 0 : number;
        latitude = latitude.add(fetch);
        longituden = longituden.add(fetch);
        Point point1 = new Point(Double.valueOf(longituden.toString()), Double.valueOf(latitude.toString()));
        Distance distance1 = new Distance(30, Metrics.KILOMETERS);
        Circle circle = new Circle(point1, distance1);

        stopWatch.start();
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius("geo:key", circle, args);
        stopWatch.stop();

        number++;
        if (number == 100) {
            StopWatch.TaskInfo[] taskInfo = stopWatch.getTaskInfo();
            StopWatch.TaskInfo max = Arrays.stream(taskInfo).max((o1, o2) -> Math.toIntExact(o1.getTimeMillis() - o2.getTimeMillis())).get();
            StopWatch.TaskInfo min = Arrays.stream(taskInfo).max((o1, o2) -> Math.toIntExact(o2.getTimeMillis() - o1.getTimeMillis())).get();
            int totalMillis = Arrays.stream(taskInfo).mapToInt(t -> Math.toIntExact(t.getTimeMillis())).reduce((x, y) -> x + y).getAsInt();

            System.out.println(max.getTimeMillis());
            System.out.println(min.getTimeMillis());
            System.out.println(totalMillis / 100);
        }

        return String.valueOf(results.getContent().size());
    }
}
