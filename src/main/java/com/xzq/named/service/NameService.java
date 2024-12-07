package com.xzq.named.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.xzq.named.constant.SourceType;
import com.xzq.named.vo.*;
import org.nlpcn.commons.lang.jianfan.JianFan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NameService {

    @Autowired
    private SourceService sourceService;

    @Autowired
    private WuGeService wuGeService;

    @Autowired
    private StrokeService strokeService;

    public List<Name> generateName(NamedCondition condition) {
        List<Name> resultList = CollUtil.newArrayList();
        Set<String> nameSet = CollUtil.newHashSet();
        List<List<Integer>> strokeList = wuGeService.listNameStroke(condition.getLastName(), condition.getMinStroke(), condition.getMaxStroke()
                , condition.isAllowGeneral(), condition.isCheckSanCai());
        condition.getSourceTypeList().forEach(s -> {
            List<Name> nameList = generateName(SourceType.getByName(s), condition, strokeList, nameSet);
            resultList.addAll(nameList);
        });
        // 按笔画排序
        return resultList.stream().sorted(Comparator.comparing(o -> o.getStrokeList().stream().reduce(Integer::sum).orElse(0))).collect(Collectors.toList());
    }

    /**
     * 生成名字
     *
     * @param sourceType   來源类型
     * @return
     */
    public List<Name> generateName(SourceType sourceType, NamedCondition condition, List<List<Integer>> strokeList, Set<String> nameSet) {

        List<Name> resultList = CollUtil.newArrayList();

        List<Source> sourceList = sourceService.listSourceByType(sourceType);

        List<String> goodList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(condition.getGoodWordList())) {
            condition.getGoodWordList().forEach(e -> goodList.add(JianFan.j2f(e)));
        }

        List<String> badList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(condition.getBadWordList())) {
            condition.getBadWordList().forEach(e -> badList.add(JianFan.j2f(e)));
        }

        for (Source source : sourceList) {
            // 过滤长度小于2的来源
            if (CollUtil.isEmpty(source.getContentList()) || source.getContentList().size() < 2) {
                continue;
            }
            for (List<Integer> strokes : strokeList) {
                if (strokes.size() != 3) {
                    continue;
                }
                int stroke1 = strokes.get(1);
                int stroke2 = strokes.get(2);
                if (source.getStrokeList().contains(stroke1) && source.getStrokeList().contains(stroke2)) {
                    int idx1 = source.getStrokeList().indexOf(stroke1);
                    int idx2 = source.getStrokeList().indexOf(stroke2);

                    // 喜欢的字过滤
                    if (CollUtil.isNotEmpty(goodList)) {
                        if (!goodList.contains(source.getContentList().get(idx1)) && !goodList.contains(source.getContentList().get(idx2))) {
                            continue;
                        }
                    }

                    // 讨厌的字
                    if (CollUtil.isNotEmpty(badList)) {
                        if (badList.contains(source.getContentList().get(idx1)) || badList.contains(source.getContentList().get(idx2))) {
                            continue;
                        }
                    }

                    if (idx1 < idx2) {
                        String firstName = JianFan.f2j(source.getContentList().get(idx1) + JianFan.f2j(source.getContentList().get(idx2)));
                        // 过滤名字
                        if (condition.isValidate() && !sourceService.validateName(firstName, condition.getSex())) {
                            continue;
                        }
                        // 去重
                        if (nameSet.contains(firstName)) {
                            continue;
                        }
                        Name name = new Name();
                        name.setFirstName(firstName);
                        String content = source.getContent();
                        content = content.replace(source.getContentList().get(idx1), "「" + source.getContentList().get(idx1) + "」");
                        content = content.replace(source.getContentList().get(idx2), "「" + source.getContentList().get(idx2) + "」");
                        name.setContent(content);
                        name.setSource(source);
                        name.setLastName(condition.getLastName());
                        name.setStrokeList(strokes);
                        resultList.add(name);
                        nameSet.add(firstName);
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * 名字查询
     *
     * @param name
     * @return
     */
    public Result searchName(String name) {

        if (StrUtil.isEmpty(name) || name.length() != 3) {
            return Result.error(1000, "请输入姓名，暂时只支持单姓复名");
        }

        List<String> strList = CollUtil.toList(name.split(""));

        List<Integer> strokeList = CollUtil.newArrayList();
        StringBuilder traditionalName = new StringBuilder();
        strList.forEach(s -> {
            String f = JianFan.j2f(s);
            strokeList.add(strokeService.getStroke(f));
            traditionalName.append(f);
        });

        Name nameVo = new Name();
        nameVo.setName(name);
        nameVo.setTraditionalName(traditionalName.toString());
        nameVo.setStrokeList(strokeList);
        nameVo.setSanCai(wuGeService.getSanCai(strokeList));
        nameVo.setWuGeList(wuGeService.getWuGeList(strokeList));

        SearchResult searchResult = new SearchResult();
        searchResult.setName(nameVo);

        List<String> traditionalList = CollUtil.toList(nameVo.getTraditionalName().split(""));
        String ming1 = traditionalList.get(1);
        String ming2 = traditionalList.get(2);

        // 查找名字来源
        List<Source> resSourceList = CollUtil.newArrayList();

        boolean isBreak = false;

        for (SourceType sourceType : SourceType.values()) {
            List<Source> sourceList = sourceService.listSourceByType(sourceType);
            if (CollUtil.isEmpty(sourceList)) {
                continue;
            }
            for (Source source : sourceList) {
                // 过滤长度小于2的来源
                if (CollUtil.isEmpty(source.getContentList()) || source.getContentList().size() < 2) {
                    continue;
                }
                int idx1 = source.getContentList().indexOf(ming1);
                int idx2 = source.getContentList().indexOf(ming2);
                if (idx1 > -1 && idx2 > -1 && idx1 < idx2) {
                    resSourceList.add(source);
                    if (resSourceList.size() >= 5) {
                        isBreak = true;
                        break;
                    }
                }
            }
            if (isBreak) {
                break;
            }
        }

        searchResult.setSourceList(resSourceList);
        return Result.success(searchResult);
    }

}
