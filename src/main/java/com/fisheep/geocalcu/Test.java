package com.fisheep.geocalcu;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.nio.charset.Charset;

public class Test {

    public static void main(String[] args) {
        // https://console.tianditu.gov.cn/data/center-data/publish/1ce7f182791c4e889a6d73e91d0e32bf

        // hutool 解析json 至 csv
        JSONArray json = (JSONArray) JSONUtil.readJSON(new File("C:\\Users\\zhang\\Desktop\\b.json"), Charset.defaultCharset());
        int i = 0;
        for (Object o : json) {
            JSONObject jsonObject = (JSONObject) o;
            String name = (String) jsonObject.get("name");
            String gbcode = (String) jsonObject.get("gbcode");
            String address = (String) jsonObject.get("address");
            if (address == null) {
                address = "-";
            }
            address = address.replaceAll("\\,", "-");
            JSONArray point = (JSONArray) jsonObject.getByPath("geojson.geometry.coordinates.[0]");
            Object lng = point.get(0);
            Object lat = point.get(1);

            File file = FileUtil.newFile("b.csv");
            FileUtil.appendUtf8String(name + "," + address + "," + gbcode + "," + lat + "," + lng + "\n", file);
            System.out.println("----:" + i);
            i++;
        }
        System.out.println("----");
    }

}
