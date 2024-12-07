package com.xzq.named.service;

import cn.hutool.core.collection.CollUtil;
import com.xzq.named.constant.JiXiong;
import com.xzq.named.constant.WuGeType;
import com.xzq.named.vo.SanCai;
import com.xzq.named.vo.WuGeVo;
import org.nlpcn.commons.lang.jianfan.JianFan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 三才五格
 */
@Service
public class WuGeService {

    @Autowired
    private StrokeService strokeService;

    // 大吉
    private static final List<Integer> goodStrokeList = CollUtil.toList(1, 3, 5, 6, 7, 8, 11, 13, 15, 16, 17, 18, 21, 23, 24, 25, 29, 31, 32, 33, 35, 37, 39, 41, 45, 47, 48, 52, 57, 61, 63, 65, 67, 68, 81);

    // 中吉
    private static final List<Integer> generalStrokeList = CollUtil.toList(27, 38, 42, 55, 58, 71, 72, 73, 77, 78);

    // 凶
    private static final List<Integer> badStrokeList = CollUtil.toList(2, 4, 9, 10, 12, 14, 19, 20, 22, 26, 28, 30, 34, 36, 40, 43, 44, 46, 49, 50, 51, 53, 54, 56, 59, 60, 62,
            64, 66, 69, 70, 74, 75, 76, 79, 80);

    // 大吉
    private static final List<String> goodWuXingList = CollUtil.toList("木木木", "木木火", "木木土", "木火木", "木火土", "木水木", "木水金", "木水水", "火木木", "火木火",
            "火木土", "火火木", "火火土", "火土火", "火土土", "火土金", "土火木", "土火火", "土火土", "土土火",
            "土土土", "土土金", "土金土", "土金金", "土金水", "金土火", "金土土", "金土金", "金金土", "金水木",
            "金水金", "水木木", "水木火", "水木土", "水木水", "水金土", "水金水", "水水木", "水水金");


    private static final List<String> generalWuXingList = CollUtil.toList("木火火", "木土火", "火木水", "火火火", "土木木", "土木火", "土土木", "金土木", "金金金", "金金水",
            "金水水", "水火木", "水土火", "水土土", "水土金", "水金金", "水水水");


    private static final List<String> badWuXingList = CollUtil.toList("木木金", "木火金", "木火水", "木土木", "木土水", "木金木", "木金火", "木金土", "木金金", "木金水",
            "木水火", "木水土", "火木金", "火火金", "火火水", "火金木", "火金火", "火金金", "火金水", "火水木",
            "火水火", "火水土", "火水金", "火水水", "土木土", "土木金", "土木水", "土火水", "土土水", "土金木",
            "土金火", "土水木", "土水火", "土水土", "土水水", "金木木", "金木火", "金木土", "金木金", "金木水",
            "金火木", "金火金", "金火水", "金金木", "金金木", "金水火", "水木金", "水火火", "水火土", "水火金",
            "水火水", "水土木", "水水土", "水金木", "水金火", "水水火", "水水土", "木木水", "木土金", "火土木",
            "火土水", "土火金", "金土水", "火金土", "土水金", "金火火", "金火土", "木土土", "金水土");

    /**
     * 根据五格获取合适的名字笔画
     *
     * @param lastName     姓
     * @param allowGeneral 是否允许中吉
     * @param checkSanCai  是否检查三才
     * @return
     */
    public List<List<Integer>> listNameStroke(String lastName, Integer minStroke, Integer maxStroke, boolean allowGeneral, boolean checkSanCai) {
        List<List<Integer>> list = new ArrayList<>();
        lastName = JianFan.j2f(lastName);
        int n = strokeService.getStroke(lastName);

        int min = null == minStroke ? 1 : minStroke;
        min = Math.min(min, 81);
        int max = null == maxStroke || maxStroke > 81 ? 81 : maxStroke;


        for (int i = min; i <= max; i++) {
            for (int j = min; j <= max; j++) {
                // 天格
                int tian = n + 1;
                // 人格
                int ren = n + i;
                // 地格
                int di = i + j;
                // 总格
                int zong = n + i + j;
                //  外格
                int wai = zong - ren + 1;

                if ((goodStrokeList.contains(ren) && goodStrokeList.contains(di) && goodStrokeList.contains(zong) && goodStrokeList.contains(wai)) ||
                        (allowGeneral && ((goodStrokeList.contains(ren) || generalStrokeList.contains(ren)) && (goodStrokeList.contains(di) || generalStrokeList.contains(di)) && (goodStrokeList.contains(zong) || generalStrokeList.contains(zong)) && (goodStrokeList.contains(wai) || generalStrokeList.contains(wai))))) {
                    if (checkSanCai) {
                        if (checkSanCai(tian, ren, di, allowGeneral)) {
                            list.add(CollUtil.toList(n, i, j));
                        }
                    } else {
                        list.add(CollUtil.toList(n, i, j));
                    }
                }
            }
        }
        return list;
    }

    /**
     * 根据笔画获取五行
     *
     * @param num
     * @return
     */
    public String getWuXingByStroke(int num) {
        num = num % 10;
        if (num == 1 || num == 2) {
            return "木";
        } else if (num == 3 || num == 4) {
            return "火";
        }
        if (num == 5 || num == 6) {
            return "土";
        }
        if (num == 7 || num == 8) {
            return "金";
        } else {
            return "水";
        }
    }

    /**
     * 获取三才
     *
     * @param i
     * @param j
     * @param k
     * @return
     */
    public String getSanCaiName(int i, int j, int k) {
        return getWuXingByStroke(i) + getWuXingByStroke(j) + getWuXingByStroke(k);
    }

    /**
     * 检查三才
     *
     * @param i
     * @param j
     * @param k
     * @param allowGeneral
     * @return
     */
    public boolean checkSanCai(int i, int j, int k, boolean allowGeneral) {
        String sanCai = getSanCaiName(i, j, k);
        if (goodWuXingList.contains(sanCai)) {
            return true;
        } else if (allowGeneral && generalWuXingList.contains(sanCai)) {
            return true;
        }
        return false;
    }

    public List<WuGeVo> getWuGeList(List<Integer> strokeList) {
        List<WuGeVo> wuGeList = CollUtil.newArrayList();
        if (CollUtil.isEmpty(strokeList) || strokeList.size() != 3) {
            return wuGeList;
        }

        int xing = strokeList.get(0);
        int ming1 = strokeList.get(1);
        int ming2 = strokeList.get(2);

        // 天格
        int tian = xing + 1;
        wuGeList.add(new WuGeVo(WuGeType.TIANGE.getName(), getJiXiong(tian).getName()));
        // 人格
        int ren = xing + ming1;
        wuGeList.add(new WuGeVo(WuGeType.RENGE.getName(), getJiXiong(ren).getName()));
        // 地格
        int di = ming1 + ming2;
        wuGeList.add(new WuGeVo(WuGeType.DIGE.getName(), getJiXiong(di).getName()));
        // 总格
        int zong = xing + ming1 + ming2;
        wuGeList.add(new WuGeVo(WuGeType.ZONGGE.getName(), getJiXiong(zong).getName()));
        // 外格
        int wai = zong - ren + 1;
        wuGeList.add(new WuGeVo(WuGeType.WAIGE.getName(), getJiXiong(wai).getName()));

        return wuGeList;
    }

    public SanCai getSanCai(List<Integer> strokeList) {
        SanCai sanCai = new SanCai();
        if (CollUtil.isEmpty(strokeList) || strokeList.size() != 3) {
            return sanCai;
        }

        int xing = strokeList.get(0);
        int ming1 = strokeList.get(1);
        int ming2 = strokeList.get(2);

        // 天格
        int tian = xing + 1;
        // 人格
        int ren = xing + ming1;
        // 地格
        int di = ming1 + ming2;

        String sanCaiName = getSanCaiName(tian, ren, di);
        sanCai.setSanCaiName(sanCaiName);

        if (goodWuXingList.contains(sanCaiName)) {
            sanCai.setJiXiong(JiXiong.DAJI.getName());
        } else if (generalWuXingList.contains(sanCaiName)) {
            sanCai.setJiXiong(JiXiong.ZHONGJI.getName());
        } else {
            sanCai.setJiXiong(JiXiong.XIONG.getName());
        }

        return sanCai;
    }

    public JiXiong getJiXiong(int n) {
        if (goodStrokeList.contains(n)) {
            return JiXiong.DAJI;
        } else if (generalStrokeList.contains(n)) {
            return JiXiong.ZHONGJI;
        } else {
            return JiXiong.XIONG;
        }
    }
}