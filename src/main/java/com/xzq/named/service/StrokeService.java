package com.xzq.named.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.*;

@Service
public class StrokeService {

    private Map<String, Integer> strokeMap;

    private Map<String, List<String>> chaiziMap;

    @PostConstruct
    public void init() {
        strokeMap = new HashMap<>();
        List<String> lines = FileUtil.readLines(ResourceUtil.getResource("data/stroke.dat"), Charset.defaultCharset());
        for (String line : lines) {
            String[] data = StrUtil.split(line, "|");
            if (data.length == 3) {
                strokeMap.put(data[1], Integer.parseInt(data[2]));
            }
        }

        chaiziMap = new HashMap<>();
        lines = FileUtil.readLines(ResourceUtil.getResource("data/chaizi-ft.dat"), Charset.defaultCharset());
        for (String line : lines) {
            String[] data = line.split("\\s+");
            if (data.length > 2) {
                List<String> chaiziList = new ArrayList<>(Arrays.asList(data).subList(1, data.length));
                chaiziMap.put(data[0], chaiziList);
            }
        }
    }

    /**
     * 获取笔画
     * @param str
     * @return
     */
    public int getStroke(String str) {
        int num = 0;
        num = getStroke4Number(str);
        return getFinalStroke(str, num);
    }

    private int getStroke4Number(String str) {
        switch (str) {
            case "一":
                return 1;
            case "二":
                return 2;
            case "三":
                return 3;
            case "四":
                return 4;
            case "五":
                return 5;
            case "六":
                return 6;
            case "七":
                return 7;
            case "八":
                return 8;
            case "九":
                return 9;
            case "十":
                return 10;
            default:
                return strokeMap.get(str) == null ? 0 : strokeMap.get(str);
        }
    }

    private int getFinalStroke(String str, int num) {
        if (!chaiziMap.containsKey(str)) {
            return num;
        }

        List<String> splits = chaiziMap.get(str);
        if (splits.contains("氵") || splits.contains("扌")) {
            num += 1;
        }
        if (StrUtil.equalsAny(splits.get(0), "玉", "示", "衣", "心")) {
            num += 1;
        }
        if (StrUtil.equals(splits.get(0), "月")) {
            num += 2;
        }
        if (splits.contains("艹")) {
            num += 3;
        }
        if (splits.contains("辶")) {
            num += 4;
        }
        if (splits.contains("邑") && splits.contains("阝")) {
            num += 5;
        }
        if (StrUtil.equals(splits.get(0), "阜")) {
            num += 6;
        }
        return num;
    }

}